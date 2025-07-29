package kr.co.loopz.payment.saga.event;

import kr.co.loopz.payment.dto.response.PurchasedObjectResponse;

import java.util.List;

public record DecreaseStockCommand(
        String userId,
        List<PurchasedObjectResponse> purchasedObjects
) {
}
