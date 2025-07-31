package kr.co.loopz.payment.saga;

import kr.co.loopz.payment.saga.command.ChangeOrderStatusCommand;
import kr.co.loopz.payment.saga.command.DecreaseStockCommand;
import kr.co.loopz.payment.saga.command.FailPaymentCommand;
import kr.co.loopz.payment.saga.command.IncreaseStockCommand;
import kr.co.loopz.payment.saga.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentSagaOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 결제 완료 이벤트를 수신하고 재고 감소 커맨드를 발행합니다.
     * @param event payment completed event
     */
    @KafkaListener(
            topics = "payment-completed-topic",
            containerFactory = "paymentSagaKafkaListenerContainerFactory"
    )
    public void onPaymentCompleted(PaymentCompleteEvent event) {
        log.info("결제 완료 이벤트 수신. 재고 감소 커맨드를 발행합니다. OrderId: {}", event.orderId());

        DecreaseStockCommand command = new DecreaseStockCommand(
                event.orderId(),
                event.userId(),
                event.objects()
        );
        kafkaTemplate.send("decrease-stock-command-topic", command);
    }

    /**
     * '재고 감소 성공' 이벤트를 수신하고 주문 상태 변경 커맨드를 발행합니다.
     * @param event stock decreased event
     */
    @KafkaListener(
            topics = "stock-decreased-topic",
            containerFactory = "paymentSagaKafkaListenerContainerFactory"
    )
    public void onStockDecreased(StockDecreasedEvent event) {
        log.info("재고 감소 성공 이벤트 수신. 주문 상태 변경 커맨드를 발행합니다. OrderId: {}", event.orderId());

        // '주문 상태 변경' 커맨드를 발행합니다.
        ChangeOrderStatusCommand command = new ChangeOrderStatusCommand(
                event.orderId()
        );
        kafkaTemplate.send("change-order-status-command-topic", command);
    }



    /**************************************실패로직-보상트랜잭션************************************************************************/

    /**
     * 재고 감소 실패 이벤트를 수신하고 결제 실패 커맨드를 발행합니다.
     * 현재는 결제 대기 상태가 default이기 때문에 fail payment command topic을 발행하지만 처리하지 않습니다.
     * 요구사항이 수정된다면 fail-payment-command-topic을 수신해 PENDING 상태의 주문을 삭제하는 로직을 추가해야 합니다.
     * @param event stock decrease failed event
     */
    @KafkaListener(
            topics = "stock-decrease-failed-topic",
            containerFactory = "paymentSagaKafkaListenerContainerFactory"
    )
    public void onStockDecreaseFailed(StockDecreaseFailedEvent event) {
        log.error("재고 감소 실패 이벤트 수신. 결제 실패 처리를 시작합니다. OrderId: {}", event.orderId());

        // 사가를 중단하고 보상 트랜잭션을 시작합니다.
        FailPaymentCommand command = new FailPaymentCommand(event.orderId(), event.reason());
        kafkaTemplate.send("fail-payment-command-topic", command);
    }


    /**
     * 주문 상태 변경 실패 이벤트를 수신하고 보상 트랜잭션을 시작합니다.
     * 마찬가지로 fail-payment-command-topic을 발행하지만 처리하지 않습니다.
     * 재고 감소 로직을 취소하기 위해 보상 트랜잭션으로 '재고 원복' 커맨드를 발행합니다.
     * @param event order status change failed event
     */
    @KafkaListener(
            topics = "order-status-change-failed-topic",
            containerFactory = "paymentSagaKafkaListenerContainerFactory"
    )
    public void onOrderStatusChangeFailed(OrderStatusChangeFailedEvent event) {
        log.error("주문 상태 변경 실패 이벤트 수신. 보상 트랜잭션을 시작합니다. OrderId: {}", event.orderId());

        // 이전 단계였던 '재고 감소'를 취소하기 위해 '재고 원복' 커맨드를 발행합니다.
        IncreaseStockCommand command = new IncreaseStockCommand(event.orderId(), event.items());
        kafkaTemplate.send("increase-stock-command-topic", command);

        // 동시에 결제 상태도 '실패'로 변경하는 커맨드를 발행합니다.
        FailPaymentCommand failPaymentCommand = new FailPaymentCommand(event.orderId(), event.reason());
        kafkaTemplate.send("fail-payment-command-topic", failPaymentCommand);
    }



}
