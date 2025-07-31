package kr.co.loopz.object.saga.event;

import java.util.List;

public record StockDecreasedEvent(
        String orderId,
        String userId,
        List<KafkaPurchasedObject> purchasedObjects
) {
}
