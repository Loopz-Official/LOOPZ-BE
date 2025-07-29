package kr.co.loopz.order.service;

import kr.co.loopz.order.saga.command.ChangeOrderStatusCommand;
import kr.co.loopz.order.saga.event.OrderCompletedEvent;
import kr.co.loopz.order.saga.event.OrderStatusChangeFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaOrderService {

    private final OrderListService orderListService;

    private final KafkaTemplate<String, Object> kafkaTemplate;


    @KafkaListener(
            topics = "change-order-status-command-topic",
            containerFactory = "orderCommandKafkaListenerContainerFactory"
    )
    @Transactional
    public void changeOrderStatus(ChangeOrderStatusCommand command) {
        try {
            log.info("주문 상태 변경 커맨드 수신. OrderId: {}", command.orderId());

            orderListService.makeOrderStatusOrdered(command.orderId());

            // 사가 흐름의 최종 성공을 알리는 '주문 완료' 이벤트를 발행
            OrderCompletedEvent event = new OrderCompletedEvent(command.orderId());
            kafkaTemplate.send("order-completed-topic", event);
            log.info("주문 완료 이벤트 발행. OrderId: {}", command.orderId());

        } catch (Exception e) {
            log.error("주문 상태 변경 실패. OrderId: {}", command.orderId(), e);
            // 실패 시 '주문 상태 변경 실패' 이벤트를 발행 (보상 트랜잭션을 위해)
            OrderStatusChangeFailedEvent event = new OrderStatusChangeFailedEvent(
                    command.orderId(),
                    e.getMessage()
            );
            kafkaTemplate.send("order-status-change-failed-topic", event);
        }
    }



}
