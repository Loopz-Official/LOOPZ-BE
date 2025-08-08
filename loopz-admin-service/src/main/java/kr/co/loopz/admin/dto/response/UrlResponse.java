package kr.co.loopz.admin.dto.response;

public record UrlResponse(
        String presignedUrl,
        String imageKey
) {
}
