package kr.co.loopz.object.dto.request;

import jakarta.validation.constraints.Min;
import kr.co.loopz.object.domain.enums.Keyword;
import kr.co.loopz.object.domain.enums.ObjectSize;
import kr.co.loopz.object.domain.enums.ObjectType;
import kr.co.loopz.object.dto.request.enums.SortType;

import java.util.HashSet;
import java.util.Set;

public record FilterRequest(
        Set<ObjectType> objectTypes,
        Set<ObjectSize> objectSizes,
        Integer priceMin,
        Integer priceMax,
        Set<Keyword> keywords,
        Boolean excludeSoldOut,
        SortType sort,
        @Min(1) int page,
        @Min(1) int size
) {

    public FilterRequest {
        objectTypes = objectTypes != null ? objectTypes : new HashSet<>();
        objectSizes = objectSizes != null ? objectSizes : new HashSet<>();
        keywords = keywords != null ? keywords : new HashSet<>();
        excludeSoldOut = excludeSoldOut != null ? excludeSoldOut : false;
        sort = sort != null ? sort : SortType.latest;
    }

    public int page() {
        return Math.max(page-1, 0);
    }

}
