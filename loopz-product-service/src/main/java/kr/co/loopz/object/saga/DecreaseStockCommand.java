package kr.co.loopz.object.saga;


import kr.co.loopz.object.dto.response.PurchasedObjectResponse;

import java.util.List;

public record DecreaseStockCommand(
        String orderId,
        String userId,
        List<PurchasedObjectResponse> purchasedObjects
) {
}
