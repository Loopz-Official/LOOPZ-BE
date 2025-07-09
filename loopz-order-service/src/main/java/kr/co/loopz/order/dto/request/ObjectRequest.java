package kr.co.loopz.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ObjectRequest(
        @NotBlank
        String objectId,
        @Min(1)
        int quantity
) {
}
