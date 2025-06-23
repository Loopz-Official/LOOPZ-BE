package kr.co.loopz.order.dto.request;

import kr.co.loopz.order.domain.enums.PaymentMethod;

public record OrderRequest(
        int quantity,
        String addressId,
        PaymentMethod paymentMethod,
        String deliveryRequest
) {
}
