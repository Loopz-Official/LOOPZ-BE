package kr.co.loopz.order.dto.response;

import kr.co.loopz.order.domain.enums.OrderStatus;

import java.time.LocalDateTime;

public record PurchasedObjectResponse(
        String objectId,
        String objectName,
        OrderStatus status,
        String intro,
        String imageUrl,
        Long purchasePrice,
        int quantity,
        LocalDateTime orderDate
) {
}
