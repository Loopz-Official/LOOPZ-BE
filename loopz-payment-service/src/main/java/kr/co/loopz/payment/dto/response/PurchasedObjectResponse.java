package kr.co.loopz.payment.dto.response;


public record PurchasedObjectResponse(
        String objectId,
        String objectName,
//        OrderStatus status,
//        String intro,
        String imageUrl,
        Long purchasePrice,
        int quantity
//        LocalDateTime orderDate
) {
}
