package kr.co.loopz.order.dto.response;

public record CartItemResponse(
        ObjectResponse object,
        int quantity
){
}
