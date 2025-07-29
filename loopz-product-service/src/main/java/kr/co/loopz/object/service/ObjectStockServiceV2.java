package kr.co.loopz.object.service;

import kr.co.loopz.object.exception.ObjectException;
import kr.co.loopz.object.saga.command.DecreaseStockCommand;
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
        }
    }


}
