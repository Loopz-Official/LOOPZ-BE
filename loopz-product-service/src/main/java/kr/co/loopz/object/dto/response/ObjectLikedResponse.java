package kr.co.loopz.object.dto.response;

public record ObjectLikedResponse(
        String objectId,
        boolean liked
) {
}
