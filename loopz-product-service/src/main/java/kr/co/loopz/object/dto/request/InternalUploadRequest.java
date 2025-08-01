package kr.co.loopz.object.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.loopz.object.domain.enums.Keyword;
import kr.co.loopz.object.domain.enums.ObjectSize;
import kr.co.loopz.object.domain.enums.ObjectType;

import java.util.List;

public record InternalUploadRequest(
        @NotBlank String objectName,
        @NotNull Long objectPrice,
        @NotBlank String intro,
        @NotNull ObjectType objectType,
        @NotNull ObjectSize objectSize,
        @NotNull List<Keyword> keywords,
        String size,
        String descriptionUrl,
        @NotBlank String imageKey,
        @Min(0) int stock
) {

}
