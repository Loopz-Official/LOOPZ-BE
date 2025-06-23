package kr.co.loopz.order.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "userServiceClient",
        url = "${etc.user-service-url}"
)
public interface UserClient {

    @GetMapping("/internal/user/{userId}/exists")
    boolean existsByUserId(@PathVariable("userId") String userId);

    @GetMapping("/internal/user/{userId}/addresses/{addressId}/exists")
    boolean existsAddressByUserId(
            @PathVariable("userId") String userId,
            @PathVariable("addressId") String addressId
    );
}