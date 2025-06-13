package kr.co.loopz.user.dto.response;

public record AddressRegisterResponse (
        String userId,
        String recipientName,
        String phoneNumber,
        String zoneCode,
        String address,
        String addressDetail,
        boolean isDefault
){
}
