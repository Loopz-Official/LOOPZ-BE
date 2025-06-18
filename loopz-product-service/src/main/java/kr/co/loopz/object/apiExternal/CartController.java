package kr.co.loopz.object.apiExternal;

import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.loopz.object.dto.request.CartSelectRequest;
import kr.co.loopz.object.dto.request.CartUpdateRequest;
import kr.co.loopz.object.dto.response.CartListResponse;
import kr.co.loopz.object.dto.response.CartResponse;
import kr.co.loopz.object.dto.response.DetailResponse;
import kr.co.loopz.object.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/object/v1/cart")
public class CartController {

    private final CartService cartService;

    @PatchMapping
    @Operation(summary = "카트 수량 변경")
    public ResponseEntity<CartResponse> updateCart(
            @AuthenticationPrincipal User currentUser,
            @RequestBody CartUpdateRequest request) {

        String userId = currentUser.getUsername();
        CartResponse response = cartService.updateCart(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    @Operation(summary="카트 조회")
    public ResponseEntity<CartListResponse> getCart(@AuthenticationPrincipal User currentUser) {

        String userId = currentUser.getUsername();
        CartListResponse response = cartService.getCart(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/selected")
    @Operation(summary="선택 상품 삭제")
    public ResponseEntity<Void> deleteSelected(@AuthenticationPrincipal User currentUser, @RequestBody List<String> objectIds){

        String userId = currentUser.getUsername();
        cartService.deleteSelected(userId,objectIds);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{objectId}")
    @Operation(summary="개별 상품 삭제")
    public ResponseEntity<Void> deleteObject(@AuthenticationPrincipal User currentUser, @PathVariable String objectId) {

        String userId = currentUser.getUsername();
        cartService.deleteCart(userId, objectId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

