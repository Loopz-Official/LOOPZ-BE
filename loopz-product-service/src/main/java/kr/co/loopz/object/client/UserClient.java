package kr.co.loopz.object.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-client",
        url = "${etc.user-service-url}"
)
public interface UserClient {

    @GetMapping("/internal/user/{userId}/exists")
    boolean existsByUserId(@PathVariable("userId") String userId);

}