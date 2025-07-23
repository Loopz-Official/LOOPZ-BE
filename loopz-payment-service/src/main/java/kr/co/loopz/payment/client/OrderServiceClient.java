package kr.co.loopz.payment.client;

import kr.co.loopz.payment.dto.response.InternalOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "order-service-client",
        url = "${etc.order-service-url}"
)
public interface OrderServiceClient {
    @GetMapping("/internal/order/{orderId}")
    InternalOrderResponse getOrderById(@PathVariable String orderId);

    @PostMapping("/internal/order/{orderId}/ordered")
    void makeOrderStatusOrdered(@PathVariable String orderId);
}
