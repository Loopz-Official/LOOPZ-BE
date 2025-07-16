package kr.co.loopz.order.dto.response;

import java.util.List;

public record OrderListResponse(
        String orderId,
        List<PurchasedObjectResponse> objects
) {
}
