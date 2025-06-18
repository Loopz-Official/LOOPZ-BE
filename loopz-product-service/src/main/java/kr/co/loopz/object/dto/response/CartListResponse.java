package kr.co.loopz.object.dto.response;

import java.util.List;

public record CartListResponse (
        List<CartItemResponse> availableItems,
        List<String> outOfStock

){
}
