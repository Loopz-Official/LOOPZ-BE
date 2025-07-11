package kr.co.loopz.order.apiExternal;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.co.loopz.order.dto.request.OrderRequest;
import kr.co.loopz.order.dto.response.OrderResponse;
import kr.co.loopz.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/order/v1")
public class OrderController {

    private final OrderService orderService;

    // 상품 상세보기에서 주문 (단일 상품)
    @PostMapping()
    @Operation(summary = "주문 API - 주문 상태 PENDING")
    public ResponseEntity<OrderResponse> orderSingle(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid OrderRequest request) {

        String userId = currentUser.getUsername();

        OrderResponse response = orderService.createOrder(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
