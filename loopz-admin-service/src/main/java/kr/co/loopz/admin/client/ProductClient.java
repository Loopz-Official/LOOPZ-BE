package kr.co.loopz.admin.client;

import kr.co.loopz.admin.dto.request.UploadRequest;
import kr.co.loopz.admin.dto.response.UploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


import java.util.List;

@FeignClient(
        name = "product-service2-client",
        url = "${etc.product-service-url}"
)
public interface ProductClient {

    @PostMapping("/internal/admin/upload")
    UploadResponse uploadObject(@RequestHeader("userId") String userId, @RequestBody UploadRequest request);



}