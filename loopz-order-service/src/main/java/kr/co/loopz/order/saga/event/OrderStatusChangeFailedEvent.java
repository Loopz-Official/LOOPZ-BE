package kr.co.loopz.order.saga.event;

import kr.co.loopz.order.dto.response.ItemIdAndQuantity;

import java.util.List;

public record OrderStatusChangeFailedEvent(
        String orderId,
        String reason,
        List<ItemIdAndQuantity> items
) {
}
