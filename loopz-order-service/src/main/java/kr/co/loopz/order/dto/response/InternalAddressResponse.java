package kr.co.loopz.order.dto.response;

public record InternalAddressResponse(
        String addressId,
        String userId,
        String recipientName,
        String phoneNumber,
        String zoneCode,
        String address,
        String addressDetail,
        boolean defaultAddress
){
}
