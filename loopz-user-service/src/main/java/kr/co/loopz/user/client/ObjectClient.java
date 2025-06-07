package kr.co.loopz.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "object-client",
        url = "${etc.product-service-url}"
)

@RequestMapping("/internal/objects")
public interface ObjectClient {
    @GetMapping("/{objectId}/exists")
    boolean existsByObjectId(@PathVariable("objectId") String objectId);

    @PostMapping("/exists")
    List<String> findExistingObjectIds(@RequestBody List<String> objectIds);
}
