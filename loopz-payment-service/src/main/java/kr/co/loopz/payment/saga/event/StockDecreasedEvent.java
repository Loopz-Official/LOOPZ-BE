package kr.co.loopz.payment.saga.event;


import java.util.List;

public record StockDecreasedEvent(
        String orderId,
        String userId,
        List<KafkaPurchasedObject> purchasedObjects
) {
}
