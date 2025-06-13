package kr.co.loopz.user.dto.request;

public record AddressRegisterRequest (
        String recipientName,
        String phoneNumber,
        String zoneCode,
        String address,
        String addressDetail,
        boolean isDefault
) {
}
