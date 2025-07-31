package kr.co.loopz.payment.saga.event;


import kr.co.loopz.payment.dto.response.PurchasedObjectResponse;

import java.util.List;

public record StockDecreasedEvent(
        String orderId,
        String userId,
        List<PurchasedObjectResponse> purchasedObjects
) {
}
