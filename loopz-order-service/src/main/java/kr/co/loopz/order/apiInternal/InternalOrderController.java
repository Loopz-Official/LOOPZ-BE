package kr.co.loopz.order.apiInternal;

import kr.co.loopz.order.dto.response.OrderListResponse;
import kr.co.loopz.order.service.OrderListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * order id에 해당하는 아이템들의 order status를 ORDERED로 변경합니다. 결제 완료 후 PaymentService에서 호출됩니다.
     * @param orderId 주문 ID
     * @return HTTP 200 OK
     */
    @PostMapping("{orderId}/ordered")
    public ResponseEntity<Void> makeOrderStatusOrdered(
            @PathVariable String orderId
    ) {
        orderListService.makeOrderStatusOrdered(orderId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
