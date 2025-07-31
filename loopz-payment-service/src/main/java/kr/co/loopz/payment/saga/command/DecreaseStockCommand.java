package kr.co.loopz.payment.saga.command;

import kr.co.loopz.payment.saga.event.KafkaPurchasedObject;

import java.util.List;

public record DecreaseStockCommand(
        String orderId,
        String userId,
        List<KafkaPurchasedObject> purchasedObjects
) {
}
