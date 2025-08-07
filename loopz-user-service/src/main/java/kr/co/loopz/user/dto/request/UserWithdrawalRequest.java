package kr.co.loopz.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserWithdrawalRequest(
        @NotBlank
        String reason
) {
}
