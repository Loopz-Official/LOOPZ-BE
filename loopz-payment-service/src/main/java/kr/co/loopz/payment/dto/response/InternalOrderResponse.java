package kr.co.loopz.payment.dto.response;

import java.util.List;

public record InternalOrderResponse(
        String orderId,
        List<PurchasedObjectResponse> objects
) {
}
