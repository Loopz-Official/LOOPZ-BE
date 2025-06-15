package kr.co.loopz.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddressRegisterRequest (
        @NotNull String recipientName,
        @NotNull String phoneNumber,
        @NotNull String zoneCode,
        @NotNull String address,
        @NotNull String addressDetail,
        boolean defaultAddress
) {
}
