package kr.co.loopz.order.dto.response;

public record ItemIdAndQuantity(
        String objectId,
        int quantity,
        Long purchasePrice
) {
}
