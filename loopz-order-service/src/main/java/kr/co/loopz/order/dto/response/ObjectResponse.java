package kr.co.loopz.order.dto.response;

public record ObjectResponse(
        String objectId,
        String objectName,
        String imageUrl,
        int objectPrice,
        int quantity,
        int totalPrice

) {
}
