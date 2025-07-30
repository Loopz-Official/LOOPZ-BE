package kr.co.loopz.object.service;

import kr.co.loopz.object.dto.response.ItemIdAndQuantity;
import kr.co.loopz.object.exception.ObjectException;
import kr.co.loopz.object.saga.command.DecreaseStockCommand;
import kr.co.loopz.object.saga.command.IncreaseStockCommand;
import kr.co.loopz.object.saga.event.StockDecreaseFailedEvent;
import kr.co.loopz.object.saga.event.StockDecreasedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ObjectStockServiceV2 {

    private final ObjectStockService objectStockService;

    private final KafkaTemplate<String, Object> kafkaTemplate;



    @KafkaListener(
            topics = "decrease-stock-command-topic",
            containerFactory = "productCommandKafkaListenerContainerFactory"
    )
    @Transactional
    public void decreaseStockAndUpdateCartV2(DecreaseStockCommand command) {

        try {
            log.info("재고 감소 커맨드 수신. 재고를 감소시키고 카트에서 삭제후 이벤트를 발행합니다.");

            objectStockService.decreaseStockAndUpdateCart(command.userId(), command.purchasedObjects());

            // 성공 시 '재고 감소 성공' 이벤트를 발행하여 오케스트레이터에게 알림
            StockDecreasedEvent event = new StockDecreasedEvent(
                    command.orderId(),
                    command.userId(),
                    command.purchasedObjects()
            );
            kafkaTemplate.send("stock-decreased-topic", event);
            log.info("재고 감소 성공 이벤트 발행. OrderId: {}", command.orderId());

        } catch (ObjectException e) {
            log.error("재고 부족으로 재고 감소 실패.", e);
            // 실패 시 '재고 감소 실패' 이벤트를 발행 (보상 트랜잭션을 위해)
            StockDecreaseFailedEvent event = new StockDecreaseFailedEvent(
                    command.orderId(),
                    "재고 부족: " + e.getMessage()
            );
            kafkaTemplate.send("stock-decrease-failed-topic", event);
        } catch (Exception e) {
            log.error("재고 감소 처리 중 알수없는 오류 발생. OrderId: {}", command.orderId(), e);
            StockDecreaseFailedEvent event = new StockDecreaseFailedEvent(
                    command.orderId(),
                    "시스템 오류: " + e.getMessage()
            );
            kafkaTemplate.send("stock-decrease-failed-topic", event);
        }
    }

    @KafkaListener(
            topics = "increase-stock-command-topic",
            containerFactory = "productCommandKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleIncreaseStockCommand(IncreaseStockCommand command) {
        log.info("재고 원복 커맨드 수신. OrderId: {}", command.orderId());
        try {
            // 커맨드에 포함된 상품 목록을 순회하며 재고 원복
            for (ItemIdAndQuantity item : command.items()) {
                objectStockService.increaseStock(item.objectId(), item.quantity());
            }
            log.info("재고 원복 성공. OrderId: {}", command.orderId());
            // 보상 트랜잭션 성공 시 별도의 이벤트를 발행할 수도 있습니다.
        } catch (Exception e) {
            log.error("재고 원복 처리 중 심각한 오류 발생. OrderId: {}", command.orderId(), e);
        }
    }


}
