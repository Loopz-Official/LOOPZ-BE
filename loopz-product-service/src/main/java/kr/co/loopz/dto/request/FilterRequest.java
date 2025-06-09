package kr.co.loopz.dto.request;

import kr.co.loopz.domain.enums.Keyword;
import kr.co.loopz.domain.enums.ObjectSize;
import kr.co.loopz.domain.enums.ObjectType;
import lombok.Data;

import java.util.Set;

@Data
public class FilterRequest {
    private Set<ObjectType> objectTypes;
    private Set<ObjectSize> objectSize;
    private Integer priceMin;
    private Integer priceMax;
    private Set<Keyword> keywords;
    private Boolean soldOut;
    private String sort;
}
