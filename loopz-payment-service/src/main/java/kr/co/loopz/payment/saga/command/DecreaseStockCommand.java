package kr.co.loopz.payment.saga.command;

import kr.co.loopz.payment.dto.response.PurchasedObjectResponse;

import java.util.List;

public record DecreaseStockCommand(
        String orderId,
        String userId,
        List<PurchasedObjectResponse> purchasedObjects
) {
}
