package kr.co.loopz.order.dto.request;

import kr.co.loopz.order.domain.enums.PaymentMethod;

import java.util.List;

public record CartOrderRequest(
        String addressId,
        PaymentMethod paymentMethod,
        String deliveryRequest,
        List<String> objectIds,
        boolean agreedToTerms
) {}
