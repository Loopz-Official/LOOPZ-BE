package kr.co.loopz.order.dto.response;

import kr.co.loopz.order.domain.enums.OrderStatus;
import kr.co.loopz.order.domain.enums.PaymentMethod;

public record ObjectDetailResponse(

        MyOrderObjectResponse objectResponse,
        InternalAddressResponse address,
        int shippingFee,
        long totalProductPrice,
        long totalPayment,
        PaymentMethod paymentMethod

) {
}
