package kr.co.loopz.authentication.client;

import kr.co.loopz.authentication.dto.request.InternalRegisterRequest;
import kr.co.loopz.authentication.dto.response.InternalRegisterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-service-client",
        url  = "${etc.user-service-url}"
)
public interface UserServiceClient {

    @PostMapping("/internal/user")
    InternalRegisterResponse getOrCreateUser(
            @RequestBody InternalRegisterRequest registerRequest
    );


}
