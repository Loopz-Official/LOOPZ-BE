package kr.co.loopz.order.client;

import kr.co.loopz.order.dto.request.DeleteCartItemRequest;
import kr.co.loopz.order.dto.response.CartWithQuantityResponse;
import kr.co.loopz.order.dto.response.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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

    // 주문 후 재고 감소
    @PostMapping("/internal/object/{objectId}/stock")
    void decreaseStock(@PathVariable String objectId, @RequestParam int quantity);

    // 카트 상품 조회
    @GetMapping("/internal/object/cart")
    boolean checkObjectInCart(@RequestParam String cartId, @RequestParam String objectId);

    @GetMapping("/internal/cart/user/{userId}")
    CartWithQuantityResponse getCartByUserId(@PathVariable String userId);

    //카트 상품 삭제
    @DeleteMapping("/internal/cart")
    void deleteCartItem(@RequestBody DeleteCartItemRequest request);
}
