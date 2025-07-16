package kr.co.loopz.order.apiExternal;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.co.loopz.order.dto.request.OrderRequest;
import kr.co.loopz.order.dto.response.OrderListResponse;
import kr.co.loopz.order.dto.response.OrderResponse;
import kr.co.loopz.order.service.OrderListService;
import kr.co.loopz.order.service.OrderService;
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
@RequestMapping(value = "/order/v1")
public class OrderController {

    private final OrderService orderService;
    private final OrderListService orderListService;

    // 상품 주문
    @PostMapping()
    @Operation(summary = "주문 API")
    public ResponseEntity<OrderResponse> orderObject(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid OrderRequest request) {

        String userId = currentUser.getUsername();

        OrderResponse response = orderService.createOrder(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 내 주문목록 조회
    @GetMapping()
    public ResponseEntity<List<OrderListResponse>> getOrders(
            @AuthenticationPrincipal User currentUser
    ) {

        String userId = currentUser.getUsername();

        List<OrderListResponse> response = orderListService.getOrders(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderListResponse> getOrder(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String orderId
    ) {

        String userId = currentUser.getUsername();

        OrderListResponse response = orderListService.getOrder(userId, orderId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
