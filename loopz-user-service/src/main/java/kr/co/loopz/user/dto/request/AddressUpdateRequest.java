package kr.co.loopz.user.dto.request;

public record AddressUpdateRequest(
        String recipientName,
        String phoneNumber,
        String zoneCode,
        String address,
        String addressDetail,
        Boolean defaultAddress
) {}
