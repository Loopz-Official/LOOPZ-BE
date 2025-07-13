package kr.co.loopz.object.dto.response;

public record OrderObjectInfoResponse (
        String objectId,
        String objectName,
        String imageUrl,
        Long objectPrice,
        int quantity,
        int stock
){
}
