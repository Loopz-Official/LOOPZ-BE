package kr.co.loopz.admin.apiExternal;

import kr.co.loopz.admin.dto.response.UrlResponse;
import kr.co.loopz.admin.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3/v1")
@PreAuthorize("hasRole('ADMIN')")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/presignedUrl")
    public ResponseEntity<UrlResponse> getPresignedUrl(@RequestParam String fileName) {

        String presignedUrl = s3Service.generatePresignedUrl(fileName);

        String imageKey = presignedUrl.substring(
                presignedUrl.indexOf(".amazonaws.com/") + ".amazonaws.com/".length(),
                presignedUrl.indexOf("?")
        );

        return ResponseEntity.ok(new UrlResponse(presignedUrl,imageKey));
    }
}
