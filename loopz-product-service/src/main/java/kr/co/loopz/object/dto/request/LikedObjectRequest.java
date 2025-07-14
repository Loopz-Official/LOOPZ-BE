package kr.co.loopz.object.dto.request;

import jakarta.validation.constraints.Min;

public record LikedObjectRequest(
        @Min(1)
        int page,
        @Min(1)
        int size,
        Boolean excludeSoldOut
) {

        public int page() {
                return Math.max(page-1, 0);
        }
}
