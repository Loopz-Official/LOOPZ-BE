package kr.co.loopz.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.portone.sdk.server.payment.PaymentMethod;
import kr.co.loopz.payment.domain.enums.OrderStatus;

import java.util.List;

public record PaymentCompleteResponse(
        @JsonIgnore
        String userId,
        PaymentMethod paymentMethod,
        OrderStatus orderStatus,
        List<PurchasedObjectResponse> objects
) {
}
