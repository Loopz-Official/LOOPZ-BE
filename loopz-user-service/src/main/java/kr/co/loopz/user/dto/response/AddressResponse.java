package kr.co.loopz.user.dto.response;

public record AddressResponse (
        Long id,
        String userId,
        String recipientName,
        String phoneNumber,
        String zoneCode,
        String address,
        String addressDetail,
        boolean isDefault
){
}
