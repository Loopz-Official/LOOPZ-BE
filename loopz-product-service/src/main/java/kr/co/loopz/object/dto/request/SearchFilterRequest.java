package kr.co.loopz.object.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchFilterRequest(
        @JsonProperty("keyword")
        String searchWord,
        String sort,
        Boolean excludeSoldOut,
        int page,
        int size
) {}
