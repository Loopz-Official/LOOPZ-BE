package kr.co.loopz.order.dto.response;

public record ObjectResponse(
        String objectId,
        String objectName,
        String imageUrl,
        Long purchasePrice,
        int quantity,
        int stock
) {
}
