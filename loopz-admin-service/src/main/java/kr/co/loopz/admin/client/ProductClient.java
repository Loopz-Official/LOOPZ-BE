package kr.co.loopz.admin.client;

import kr.co.loopz.admin.dto.request.UploadRequest;
import kr.co.loopz.admin.dto.response.UploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@FeignClient(
        name = "product-service2-client",
        url = "${etc.product-service-url}"
)
public interface ProductClient {

    @PostMapping("/internal/admin/upload")
    UploadResponse uploadObject(@RequestHeader("Authorization") String jwtToken, @RequestBody UploadRequest request);

    @PutMapping("/internal/admin/modify/{objectId}")
    UploadResponse modifyObject(@RequestHeader("Authorization") String jwtToken,  @PathVariable String objectId, @RequestBody UploadRequest request);

    @DeleteMapping("/internal/admin/{objectId}")
    String deleteObject(@RequestHeader("Authorization") String jwtToken,  @PathVariable String objectId);

}