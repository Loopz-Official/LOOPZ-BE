package kr.co.loopz.search.client;

import kr.co.loopz.search.dto.response.ObjectNameResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "product-service-client",
        url = "${etc.product-service-url}"
)
public interface ProductClient {

    @GetMapping("/internal/object/search")
    List<ObjectNameResponse> findProductNamesByKeyword(@RequestParam("keyword") String keyword);


}