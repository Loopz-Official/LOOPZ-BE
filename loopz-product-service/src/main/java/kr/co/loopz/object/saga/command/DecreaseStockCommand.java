package kr.co.loopz.object.saga.command;


import kr.co.loopz.object.saga.event.KafkaPurchasedObject;

import java.util.List;

public record DecreaseStockCommand(
        String orderId,
        String userId,
        List<KafkaPurchasedObject> purchasedObjects
) {
}
