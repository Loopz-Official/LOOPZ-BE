package kr.co.loopz.order.service;

import kr.co.loopz.order.client.UserClient;
import kr.co.loopz.order.converter.OrderConverter;
import kr.co.loopz.order.domain.Order;
import kr.co.loopz.order.domain.OrderItem;
import kr.co.loopz.order.dto.response.InternalAddressResponse;
import kr.co.loopz.order.dto.response.InternalObjectResponse;
import kr.co.loopz.order.dto.response.OrderDetailResponse;
import kr.co.loopz.order.exception.OrderException;
import kr.co.loopz.order.repository.OrderItemRepository;
import kr.co.loopz.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.co.loopz.order.constants.OrderConstants.SHIPPING_FEE;
import static kr.co.loopz.order.exception.OrderErrorCode.ORDER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderListService orderListService;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final UserClient userClient;
    private final OrderConverter orderConverter;

    public OrderDetailResponse getOrderDetail(String userId, String orderId) {

        // userId + orderId로 주문 확인
        Order order = getOrder(orderId, userId);

        // 주문의 OrderItem 조회
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        // 상품 상세 조회
        List<InternalObjectResponse> objectDetails = orderListService.getObjectDetailsByOrderItems(orderItems);

        // 주소 조회
        InternalAddressResponse address = userClient.getAddressById(order.getUserId(), order.getAddressId());

        long totalProductPrice = calculateTotalProductPrice(orderItems);

        return orderConverter.toObjectDetailResponse(order, orderItems, objectDetails, address, SHIPPING_FEE, totalProductPrice, totalProductPrice+SHIPPING_FEE);

    }

    private long calculateTotalProductPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToLong(item -> item.getPurchasePrice() * item.getQuantity())
                .sum();
    }

    private Order getOrder(String orderId, String userId) {
        return orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND, "orderId: " + orderId));
    }

}
