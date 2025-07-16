package kr.co.loopz.order.dto.response;

import kr.co.loopz.order.domain.enums.OrderStatus;

import java.time.LocalDateTime;

public record MyOrderObjectResponse(
        String objectId,
        String objectName,
        OrderStatus status,
        String imageUrl,
        Long totalPrice,
        int quantity,
        LocalDateTime orderDate
) {
}
