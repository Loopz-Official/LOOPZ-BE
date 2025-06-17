package kr.co.loopz.object.apiExternal;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.loopz.object.dto.request.CartUpdateRequest;
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
}

