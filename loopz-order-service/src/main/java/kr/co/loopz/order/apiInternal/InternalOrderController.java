package kr.co.loopz.order.apiInternal;

import kr.co.loopz.order.dto.response.OrderListResponse;
import kr.co.loopz.order.service.OrderListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/order")
@RequiredArgsConstructor
@Slf4j
public class InternalOrderController {

    private final OrderListService orderListService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderListResponse> getOrderById(
            @PathVariable String orderId
    ) {
        OrderListResponse response = orderListService.getOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
