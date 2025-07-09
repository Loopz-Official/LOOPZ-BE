package kr.co.loopz.order.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.loopz.order.domain.enums.PaymentMethod;

import java.util.List;

public record OrderRequest(
        @NotNull
        List<ObjectRequest> objects,
        @NotBlank
        String addressId,
        @NotNull
        PaymentMethod paymentMethod,
        String deliveryRequest,
        @AssertTrue
        boolean agreedToTerms
) {
}
