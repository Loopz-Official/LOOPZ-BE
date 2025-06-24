package kr.co.loopz.order.dto.response;

import java.util.List;

public record CartWithQuantityResponse(
    String cartId,
    List<CartItemResponse> items
) {}
