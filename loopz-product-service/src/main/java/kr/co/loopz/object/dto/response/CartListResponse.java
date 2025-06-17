package kr.co.loopz.object.dto.response;

import java.util.List;

public record CartListResponse (
        List<CartItemResponse> items,
        int totalQuantity,
        long totalPrice,
        int shippingfee,
        long finalPrice
){
}
