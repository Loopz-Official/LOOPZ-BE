package kr.co.loopz.search.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record SearchFilterRequest(
        @NotBlank String keyword,
        String sort,
        Boolean excludeSoldOut,
        int page,
        int size
) {
    public int page() {
        return Math.max(page - 1, 0);
    }

    public int size() {
        return size > 0 ? size : 10;  // 기본값 10
    }
}
