package kr.co.loopz.search.dto.request;

import java.util.List;

public record SearchFilterRequest(
        String keyword,
        String sort,
        Boolean excludeSoldOut,
        int page,
        int size
) {}
