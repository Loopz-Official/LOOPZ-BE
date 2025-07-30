package kr.co.loopz.payment.dto.response;

public record ItemIdAndQuantity(
        String objectId,
        int quantity,
        Long purchasePrice
) {
}
