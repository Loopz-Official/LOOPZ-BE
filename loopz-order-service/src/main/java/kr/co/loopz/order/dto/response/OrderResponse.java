package kr.co.loopz.order.dto.response;

import kr.co.loopz.order.domain.enums.OrderStatus;
import kr.co.loopz.order.domain.enums.PaymentMethod;

import java.util.List;

public record OrderResponse(
        String orderId,
        OrderStatus status,
        PaymentMethod paymentMethod,
        List<ObjectResponse> objects,
        int shippingFee,
        long totalProductPrice,
        long totalPayment
) {
}