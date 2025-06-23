package kr.co.loopz.order.client;

import kr.co.loopz.order.dto.response.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-client",
        url = "${etc.product-service-url}"
)
public interface ProductClient {

    @GetMapping("/internal/object/{objectId}")
    ObjectResponse getObjectById(@PathVariable("objectId") String objectId);

    // 재고 조회
    @GetMapping("/internal/object/{objectId}/stock")
    int getStockByObjectId(@PathVariable String objectId);
}
