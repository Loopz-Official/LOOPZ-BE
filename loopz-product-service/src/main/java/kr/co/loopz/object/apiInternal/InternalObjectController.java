package kr.co.loopz.object.apiInternal;

import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.domain.Cart;
import kr.co.loopz.object.dto.request.DeleteCartItemRequest;
import kr.co.loopz.object.dto.request.SearchFilterRequest;
import kr.co.loopz.object.dto.response.*;
import kr.co.loopz.object.repository.CartItemRepository;
import kr.co.loopz.object.repository.CartRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import kr.co.loopz.object.service.CartService;
import kr.co.loopz.object.service.ObjectDetailService;
import kr.co.loopz.object.service.ObjectListService;
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
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ObjectDetailService objectDetailService;
    private final ObjectListService objectListService;

    // objectId 존재 여부 확인 API
    @GetMapping("/objects/{objectId}/exists")
    public ResponseEntity<Boolean> checkObjectIdExists(@PathVariable String objectId) {
        boolean exists = objectRepository.existsByObjectId(objectId);
        return ResponseEntity.ok(exists);
    }


    @GetMapping("/object/{objectId}")
    public ResponseEntity<ObjectResponse> getObjectById(@PathVariable String objectId) {
        ObjectResponse response = objectDetailService.getObjectById(objectId);
        return ResponseEntity.ok(response);
    }

    // 남은 재고 확인
    @GetMapping("/object/{objectId}/stock")
    public int getStock(@PathVariable String objectId) {
        return objectDetailService.getStock(objectId);
    }

    // 재고 감소
    @PostMapping("/object/{objectId}/stock")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable String objectId,
            @RequestParam int quantity
    ) {
        objectDetailService.decreaseStock(objectId, quantity);
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

    // 장바구니 상품 삭제
    @DeleteMapping("/cart")
    public ResponseEntity<Void> deleteCartItem(@RequestBody DeleteCartItemRequest request) {
        cartService.deleteSelected(request.userId(), List.of(request.objectId()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/object/search")
    public ResponseEntity<List<ObjectNameResponse>> searchObjectsByKeyword(@RequestParam String keyword) {
        List<ObjectNameResponse> nameResponse = objectListService.searchObjectsByKeyword(keyword);
        return ResponseEntity.ok(nameResponse);
    }

    @PostMapping("/object/search")
    public ResponseEntity<BoardResponse> searchObjects(
            @RequestParam(name = "userId", required = false) String userId,
            @AuthenticationPrincipal User currentUser,
            @RequestBody SearchFilterRequest filter) {

        if (currentUser != null && userId == null) {
            userId = currentUser.getUsername();
        }


        BoardResponse result = objectListService.searchObjectsByKeyword(userId, filter);
        return ResponseEntity.ok(result);
    }
}
