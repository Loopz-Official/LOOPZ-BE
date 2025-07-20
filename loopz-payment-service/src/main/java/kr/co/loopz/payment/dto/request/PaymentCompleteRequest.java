package kr.co.loopz.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PaymentCompleteRequest(
        @NotBlank
        String paymentId
) {
}
