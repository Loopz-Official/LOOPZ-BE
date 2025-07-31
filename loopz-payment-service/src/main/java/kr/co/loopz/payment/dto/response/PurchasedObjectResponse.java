package kr.co.loopz.payment.dto.response;


public record PurchasedObjectResponse(
        String objectId,
        String objectName,
        String imageUrl,
        Long purchasePrice,
        int quantity
) {
}
