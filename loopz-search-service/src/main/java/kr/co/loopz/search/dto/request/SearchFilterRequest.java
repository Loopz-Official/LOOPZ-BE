package kr.co.loopz.search.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record SearchFilterRequest(
        @NotBlank String keyword,
        String sort,
        Boolean excludeSoldOut,
        int page,
        int size
) {}
