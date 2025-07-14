package kr.co.loopz.order.dto.response;

import kr.co.loopz.order.domain.enums.OrderStatus;
import kr.co.loopz.order.domain.enums.PaymentMethod;

import java.util.List;

public record OrderListResponse(
        String orderId,
        List<MyOrderObjectResponse> objects
) {
}
