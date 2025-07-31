package kr.co.loopz.object.dto.response;

import jakarta.validation.constraints.Min;
import kr.co.loopz.object.domain.enums.Keyword;
import kr.co.loopz.object.domain.enums.ObjectSize;
import kr.co.loopz.object.domain.enums.ObjectType;

import java.util.List;

public record InternalUploadResponse(
        String objectName,
        Long objectPrice,
        String intro,
        ObjectType objectType,
        ObjectSize objectSize,
        List<Keyword> keywords,
        String size,
        String descriptionUrl,
        String imageUrl,
        @Min(0) int stock
) {

}
