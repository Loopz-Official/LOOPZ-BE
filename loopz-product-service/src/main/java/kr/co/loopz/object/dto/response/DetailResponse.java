package kr.co.loopz.object.dto.response;

public record DetailResponse(
        String objectId,
        String objectName,
        String intro,
        String imageUrl,
        Long objectPrice,
        Boolean liked,
        int stock,
        String size,
        String descriptionUrl
) {
}