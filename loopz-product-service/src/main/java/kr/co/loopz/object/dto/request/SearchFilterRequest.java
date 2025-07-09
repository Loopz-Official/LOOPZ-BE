package kr.co.loopz.object.dto.request;

public record SearchFilterRequest(
        String keyword,
        String sort,
        Boolean excludeSoldOut,
        int page,
        int size
) {}
