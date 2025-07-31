package kr.co.loopz.object.saga.event;

public record KafkaPurchasedObject(
        String objectId,
        String objectName,
        String imageUrl,
        int purchasePrice,
        int quantity
) {
}
