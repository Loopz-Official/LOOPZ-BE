package kr.co.loopz.object.dto.request;

public record CartUpdateRequest(
        String objectId,
        int quantity
) {
}
