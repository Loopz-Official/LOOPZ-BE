package kr.co.loopz.object.apiInternal;

import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.domain.Cart;
import kr.co.loopz.object.dto.response.CartItemResponse;
import kr.co.loopz.object.dto.response.CartResponse;
import kr.co.loopz.object.dto.response.CartWithQuantityResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.repository.CartItemRepository;
import kr.co.loopz.object.repository.CartRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import kr.co.loopz.object.service.CartService;
import kr.co.loopz.object.service.ObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static kr.co.loopz.object.Exception.ObjectErrorCode.CART_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalObjectController {
    private final ObjectRepository objectRepository;
    private final ObjectService objectService;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;

    // objectId 존재 여부 확인 API
    @GetMapping("/objects/{objectId}/exists")
    public ResponseEntity<Boolean> checkObjectIdExists(@PathVariable String objectId) {
        boolean exists = objectRepository.existsByObjectId(objectId);
        return ResponseEntity.ok(exists);
    }


    @GetMapping("/object/{objectId}")
    public ResponseEntity<ObjectResponse> getObjectById(@PathVariable String objectId) {
        ObjectResponse response = objectService.getObjectById(objectId);
        return ResponseEntity.ok(response);
    }

    // 남은 재고 확인
    @GetMapping("/object/{objectId}/stock")
    public int getStock(@PathVariable String objectId) {
        return objectService.getStock(objectId);
    }

    // 재고 감소
    @PostMapping("/object/{objectId}/stock")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable String objectId,
            @RequestParam int quantity
    ) {
        objectService.decreaseStock(objectId, quantity);
        return ResponseEntity.ok().build();
    }

    // 카트 상품 존재여부 확인
    @GetMapping("/object/cart")
    public ResponseEntity<Boolean> checkObjectInCart(
            @RequestParam String cartId,
            @RequestParam String objectId) {

        boolean exists = cartItemRepository.existsByCartIdAndObjectId(cartId, objectId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/cart/user/{userId}")
    public ResponseEntity<CartWithQuantityResponse> getCartByUserId(@PathVariable String userId) {
        CartWithQuantityResponse cartResponse = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartResponse);
    }

}
