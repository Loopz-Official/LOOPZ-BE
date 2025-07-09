package kr.co.loopz.order.dto.response;

public record InternalObjectResponse(
        String objectId,
        String objectName,
        String intro,
        String imageUrl,
        Long objectPrice,
        boolean liked,
        int stock
) {
}
