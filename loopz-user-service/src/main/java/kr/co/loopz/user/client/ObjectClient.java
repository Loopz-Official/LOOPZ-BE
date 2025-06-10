package kr.co.loopz.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "object-client",
        url = "${etc.product-service-url}"
)


public interface ObjectClient {
    @GetMapping("/internal/objects/{objectId}/exists")
    boolean existsByObjectId(@PathVariable("objectId") String objectId);

}
