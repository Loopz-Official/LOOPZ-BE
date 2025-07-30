package kr.co.loopz.object.dto.response;

public record ItemIdAndQuantity(
        String objectId,
        int quantity,
        Long purchasePrice
) {
}
