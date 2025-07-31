package kr.co.loopz.object.saga.command;


import kr.co.loopz.object.dto.response.ItemIdAndQuantity;

import java.util.List;

public record IncreaseStockCommand(
        String orderId,
        List<ItemIdAndQuantity> items
) {
}
