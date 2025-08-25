package kr.co.loopz.object.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "Admin-service-client",
        url = "${etc.admin-service-url}"
)
public interface S3Client {

    @DeleteMapping("/internal/admin/s3/delete")
    void deleteFile(@RequestParam String key);
}
