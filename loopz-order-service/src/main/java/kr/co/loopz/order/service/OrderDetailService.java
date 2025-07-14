package kr.co.loopz.order.service;

import kr.co.loopz.order.client.ObjectClient;
import kr.co.loopz.order.client.UserClient;
import kr.co.loopz.order.converter.OrderConverter;
import kr.co.loopz.order.domain.Order;
import kr.co.loopz.order.domain.OrderItem;
import kr.co.loopz.order.domain.enums.PaymentMethod;
import kr.co.loopz.order.dto.response.*;
import kr.co.loopz.order.dto.response.InternalAddressResponse;
import kr.co.loopz.order.exception.OrderException;
import kr.co.loopz.order.repository.OrderItemRepository;
import kr.co.loopz.order.repository.OrderRepository;
import static kr.co.loopz.order.constants.OrderConstants.SHIPPING_FEE;
import lombok.RequiredArgsConstructor;
import org.hibernate.Internal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.co.loopz.order.exception.OrderErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ObjectClient objectClient;
    private final UserClient userClient;
    private final OrderConverter orderConverter;

    public ObjectDetailResponse getOrderDetail(String userId, String orderId, String objectId) {

        // userId + orderId로 주문 확인
        Order order = getOrder(orderId, userId);

        // 주문의 OrderItem 조회
        OrderItem orderItem = getOrderItem(orderId, objectId);

        // 상품 상세 조회
        InternalObjectResponse objectDetail = objectClient.getObjectById(objectId);

        // 주소 조회
        InternalAddressResponse address = userClient.getAddressById(order.getUserId(), order.getAddressId());

        PaymentMethod paymentMethod = order.getPaymentMethod();

        long productPrice = orderItem.getPurchasePrice() * orderItem.getQuantity();
        long totalPayment = productPrice + SHIPPING_FEE;

        MyOrderObjectResponse objectResponse = orderConverter.toMyOrderObjectResponse(orderItem, objectDetail);

        return new ObjectDetailResponse(
                objectResponse,
                address,
                SHIPPING_FEE,
                productPrice,
                totalPayment,
                paymentMethod
        );
    }

    private Order getOrder(String orderId, String userId) {
        return orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND, "orderId: " + orderId));
    }

    private OrderItem getOrderItem(String orderId, String objectId) {
        return orderItemRepository.findByOrderIdAndObjectId(orderId, objectId)
                .orElseThrow(() -> new OrderException(OBJECT_ID_NOT_FOUND, "objectId: " + objectId));
    }
}
