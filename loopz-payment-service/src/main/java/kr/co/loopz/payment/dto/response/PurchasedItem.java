package kr.co.loopz.payment.dto.response;

public record PurchasedItem(
        String productId,
        String productName,
        int quantity,
        int price,
        String currency
) {
}
