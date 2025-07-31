package kr.co.loopz.payment.saga.command;

import kr.co.loopz.payment.dto.response.ItemIdAndQuantity;

import java.util.List;

public record IncreaseStockCommand(
        String orderId,
        List<ItemIdAndQuantity> items
) {
}
