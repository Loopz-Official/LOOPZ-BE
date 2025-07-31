package kr.co.loopz.order.service;

import kr.co.loopz.order.domain.OrderItem;
import kr.co.loopz.order.dto.response.ItemIdAndQuantity;
import kr.co.loopz.order.saga.command.ChangeOrderStatusCommand;
import kr.co.loopz.order.saga.event.OrderStatusChangeFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

        List<OrderItem> orderItems = new ArrayList<>();
        try {
            log.info("주문 상태 변경 커맨드 수신. OrderId: {}", command.orderId());

            orderItems = orderListService.makeOrderStatusOrdered(command.orderId());

            // 사가 흐름의 최종 성공을 알리는 '주문 완료' 이벤트를 발행
            log.info("주문 상태 변경 성공. 사가 트랜잭션을 성공적으로 종료합니다. OrderId: {}", command.orderId());

        } catch (Exception e) {
            log.error("주문 상태 변경 실패. OrderId: {}", command.orderId(), e);
            // 실패 시 '주문 상태 변경 실패' 이벤트를 발행 (보상 트랜잭션을 위해)

            List<ItemIdAndQuantity> items = orderItems.stream()
                    .map(item -> new ItemIdAndQuantity(
                            item.getObjectId(),
                            item.getQuantity(),
                            item.getPurchasePrice()
                    )).toList();

            OrderStatusChangeFailedEvent event = new OrderStatusChangeFailedEvent(
                    command.orderId(),
                    e.getMessage(),
                    items
            );
            kafkaTemplate.send("order-status-change-failed-topic", event);
        }
    }



}
