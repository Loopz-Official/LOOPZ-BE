package kr.co.loopz.payment.saga;

import kr.co.loopz.payment.saga.event.DecreaseStockCommand;
import kr.co.loopz.payment.saga.event.PaymentCompleteEvent;
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


}
