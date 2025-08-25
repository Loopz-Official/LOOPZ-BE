package kr.co.loopz.admin.apiInternal;

import kr.co.loopz.admin.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/s3")
@RequiredArgsConstructor
public class InternalS3Controller {

    private final S3Service s3Service;

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String key) {
        s3Service.deleteFile(key);
        return ResponseEntity.ok("S3 객체 삭제 완료: " + key);
    }
}
