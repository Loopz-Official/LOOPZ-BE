package kr.co.loopz.order.dto.response;

import kr.co.loopz.order.domain.enums.PaymentMethod;

import java.util.List;

public record OrderDetailResponse(
        String orderId,
        String orderNumber,
        PaymentMethod paymentMethod,
        List<PurchasedObjectResponse> objects,
        InternalAddressResponse address,
        int shippingFee,
        long totalProductPrice,
        long totalPayment
) {
}
