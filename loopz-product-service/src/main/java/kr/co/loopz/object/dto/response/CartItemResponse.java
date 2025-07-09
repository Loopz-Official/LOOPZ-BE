package kr.co.loopz.object.dto.response;

public record CartItemResponse (
        ObjectResponse object,
        int quantity
){
}
