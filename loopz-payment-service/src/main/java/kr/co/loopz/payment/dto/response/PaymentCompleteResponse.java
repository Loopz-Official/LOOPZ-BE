package kr.co.loopz.payment.dto.response;

import io.portone.sdk.server.payment.PaymentMethod;
import kr.co.loopz.payment.domain.enums.OrderStatus;

import java.util.List;

public record PaymentCompleteResponse(
        PaymentMethod paymentMethod,
        OrderStatus orderStatus,
        List<PurchasedObjectResponse> objects
) {
}
