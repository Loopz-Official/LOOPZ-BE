package kr.co.loopz.client;

import kr.co.loopz.dto.request.LikeCheckRequest;
import kr.co.loopz.dto.response.InternalLikeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "user-client",
        url = "${etc.user-service-url}"
)
public interface UserClient {

    @PostMapping("/internal/likes")
    InternalLikeResponse checkLikes(
            @RequestHeader String userId,
            @RequestBody LikeCheckRequest request
    );

}