package kr.co.loopz.object.dto.response;

import java.util.List;

public record CartWithQuantityResponse (
    String cartId,
    List<CartItemResponse> items
) {}
