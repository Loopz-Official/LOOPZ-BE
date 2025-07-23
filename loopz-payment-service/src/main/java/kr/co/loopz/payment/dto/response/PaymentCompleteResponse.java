package kr.co.loopz.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentMethod;
import kr.co.loopz.payment.domain.enums.OrderStatus;

import java.util.List;

public record PaymentCompleteResponse(
        @JsonIgnore
        String userId,
        @JsonIgnore
        String orderId,
        @JsonIgnore
        PaidPayment paidPayment,
        PaymentMethod paymentMethod,
        OrderStatus orderStatus,
        List<PurchasedObjectResponse> objects
) {
}
