package kr.co.loopz.payment.saga.event;

import kr.co.loopz.payment.dto.response.ItemIdAndQuantity;

import java.util.List;

public record OrderStatusChangeFailedEvent(
        String orderId,
        String reason,
        List<ItemIdAndQuantity> items
) {
}
