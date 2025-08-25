package kr.co.loopz.admin.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.loopz.admin.domain.enums.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public record UploadRequest(
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
