package kr.co.loopz.dto.request;

import jakarta.validation.constraints.Min;
import kr.co.loopz.domain.enums.Keyword;
import kr.co.loopz.domain.enums.ObjectSize;
import kr.co.loopz.domain.enums.ObjectType;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class FilterRequest {

    private Set<ObjectType> objectTypes = new HashSet<>();
    private Set<ObjectSize> objectSizes = new HashSet<>();
    private Integer priceMin;
    private Integer priceMax;
    private Set<Keyword> keywords = new HashSet<>();
    private Boolean soldOut = false;
    private String sort = "latest";

    @Min(0)
    private int page = 0;

    @Min(1)
    private int size = 10;
}