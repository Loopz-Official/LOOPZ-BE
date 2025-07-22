package kr.co.loopz.payment.client;

import kr.co.loopz.payment.dto.response.PurchasedObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "product-service-client",
        url = "${etc.product-service-url}"
)
public interface ProductServiceClient {

    /**
     * 결제된 상품의 재고를 감소시키고, 장바구니에 있다면 상품을 삭제합니다.
     * @param userId 사용자 ID
     * @param purchasedObjects 결제된 상품 목록
     * @return void
     */
    @PostMapping("/internal/object/stock")
    ResponseEntity<Void> decreaseStockAndDeleteCart(@RequestHeader String userId, @RequestBody List<PurchasedObjectResponse> purchasedObjects);
}
