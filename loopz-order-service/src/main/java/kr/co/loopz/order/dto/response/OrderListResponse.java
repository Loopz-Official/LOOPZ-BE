package kr.co.loopz.order.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderListResponse(
        String orderId,
        OffsetDateTime orderDate,
        List<PurchasedObjectResponse> objects
) {
}
