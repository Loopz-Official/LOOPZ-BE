package kr.co.loopz.object.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.loopz.object.dto.request.enums.SortType;

public record SearchFilterRequest(
        @JsonProperty("keyword")
        String searchWord,
        SortType sort,
        Boolean excludeSoldOut,
        int page,
        int size
) {}
