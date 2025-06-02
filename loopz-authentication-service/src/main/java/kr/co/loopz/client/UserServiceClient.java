package kr.co.loopz.client;

import kr.co.loopz.dto.request.InternalRegisterRequest;
import kr.co.loopz.dto.response.InternalRegisterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "user-service-client",
        url  = "${etc.user-service-url}"
)
public interface UserServiceClient {

    @PostMapping("/internal/user/v1")
    InternalRegisterResponse getOrCreateUser(InternalRegisterRequest registerRequest);

}
