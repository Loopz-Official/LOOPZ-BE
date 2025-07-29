package kr.co.loopz.object.saga.event;

import kr.co.loopz.object.dto.response.PurchasedObjectResponse;

import java.util.List;

public record StockDecreasedEvent(
        String orderId,
        String userId,
        List<PurchasedObjectResponse> purchasedObjects
) {
}
