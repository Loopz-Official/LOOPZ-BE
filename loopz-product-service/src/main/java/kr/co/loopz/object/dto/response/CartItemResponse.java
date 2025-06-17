package kr.co.loopz.object.dto.response;

public record CartItemResponse (
        String objectId,
        String objectName,
        Long price,
        int quantity,
        Long totalPrice,
        String imageUrl,
        boolean selected
){
}
