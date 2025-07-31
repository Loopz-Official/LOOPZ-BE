package kr.co.loopz.payment.saga.event;

public record KafkaPurchasedObject(
        String objectId,
        String objectName,
        String imageUrl,
        int purchasePrice,
        int quantity
) {
}
