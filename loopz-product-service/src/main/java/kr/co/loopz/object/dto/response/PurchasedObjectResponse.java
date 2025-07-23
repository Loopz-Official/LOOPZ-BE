package kr.co.loopz.object.dto.response;


public record PurchasedObjectResponse(
        String objectId,
        String objectName,
        String imageUrl,
        Long purchasePrice,
        int quantity
) {
}
