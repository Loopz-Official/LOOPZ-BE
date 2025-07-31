package kr.co.loopz.admin.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.loopz.admin.domain.enums.Keyword;
import kr.co.loopz.admin.domain.enums.ObjectSize;
import kr.co.loopz.admin.domain.enums.ObjectType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UploadResponse(
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
