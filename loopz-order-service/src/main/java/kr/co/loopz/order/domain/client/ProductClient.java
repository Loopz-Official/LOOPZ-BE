package kr.co.loopz.order.domain.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "product-client",
        url = "${etc.product-service-url}"
)
public interface ProductClient {
}
