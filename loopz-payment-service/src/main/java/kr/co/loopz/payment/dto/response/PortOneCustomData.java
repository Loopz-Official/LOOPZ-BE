package kr.co.loopz.payment.dto.response;

import java.util.List;

public record PortOneCustomData(
        String orderId,
        List<PurchasedItem> purchasedItems
) {
}
