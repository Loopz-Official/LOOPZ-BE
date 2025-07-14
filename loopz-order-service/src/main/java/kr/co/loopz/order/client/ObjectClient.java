package kr.co.loopz.order.client;

import kr.co.loopz.order.dto.request.DeleteCartItemRequest;
import kr.co.loopz.order.dto.response.CartWithQuantityResponse;
import kr.co.loopz.order.dto.response.InternalObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "product-client",
        url = "${etc.product-service-url}"
)
public interface ObjectClient {

    /**
     * 상품 ID 목록을 받아 해당 상품들의 정보를 조회
     * 주문이 들어온 상품의 정보를 받아올때 사용
     * @param requestIds 상품 ID 목록
     * @return 상품 정보 리스트 (상품 UUID, 이름, 재고, 가격)
     */
    @PostMapping("/internal/objects")
    List<InternalObjectResponse> getObjectListByIds(@RequestBody List<String> requestIds);

    @PostMapping("/internal/object/{objectId}")
    InternalObjectResponse getObjectById(@PathVariable String objectId);

    // 주문 후 재고 감소
    @PostMapping("/internal/object/{objectId}/stock")
    void decreaseStock(@PathVariable String objectId, @RequestParam int quantity);

    // 카트 상품 조회
    @GetMapping("/internal/object/cart")
    boolean checkObjectInCart(@RequestParam String cartId, @RequestParam String objectId);

    // 사용자 카트 존재여부 조회
    @GetMapping("/internal/cart/user/{userId}")
    CartWithQuantityResponse getCartByUserId(@PathVariable String userId);

    //카트 상품 삭제
    @DeleteMapping("/internal/cart")
    void deleteCartItem(@RequestBody DeleteCartItemRequest request);
}
