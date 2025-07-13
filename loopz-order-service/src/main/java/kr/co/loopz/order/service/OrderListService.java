package kr.co.loopz.order.service;

import kr.co.loopz.order.client.ObjectClient;
import kr.co.loopz.order.converter.OrderConverter;
import kr.co.loopz.order.domain.Order;
import kr.co.loopz.order.domain.OrderItem;
import kr.co.loopz.order.dto.response.InternalObjectResponse;
import kr.co.loopz.order.dto.response.MyOrderObjectResponse;
import kr.co.loopz.order.dto.response.OrderListResponse;
import kr.co.loopz.order.exception.OrderException;
import kr.co.loopz.order.repository.OrderItemRepository;
import kr.co.loopz.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.co.loopz.order.exception.OrderErrorCode.OBJECT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderListService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ObjectClient objectClient;
    private final OrderConverter orderConverter;

    /**
     * 상품별 주문 상태 반환
     * object service 호출(상품 정보 조회)
     *
     * @param userId 사용자 UUID
     * @return 나의 주문 목록
     */

    public List<OrderListResponse> getOrders(String userId) {

        List<Order> orders = orderRepository.findAllByUserId(userId);

        return orders.stream()
                .map(order -> toOrderListResponse(order))
                .toList();
    }

    private OrderListResponse toOrderListResponse(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getOrderId());

        List<InternalObjectResponse> currentObjects = getObjectDetailsByOrderItems(orderItems);

        // Map으로 변환
        Map<String, InternalObjectResponse> objectDetailsMap = currentObjects.stream()
                .collect(Collectors.toMap(InternalObjectResponse::objectId, Function.identity()));

        // MyOrderObjectResponse 리스트 생성
        List<MyOrderObjectResponse> objectResponses = orderItems.stream()
                .map(item -> {
                    InternalObjectResponse detail = objectDetailsMap.get(item.getObjectId());
                    if (detail == null) {
                        throw new OrderException(OBJECT_ID_NOT_FOUND, "ObjectId:" + item.getObjectId());
                    }
                    return orderConverter.toMyOrderObjectResponse(item, detail);
                })
                .toList();

        return orderConverter.toOrderListResponse(order, objectResponses);
    }

    //  상품 상세 정보 호출 (object 서비스 호출)
    private List<InternalObjectResponse> getObjectDetailsByOrderItems(List<OrderItem> orderItems) {
        List<String> objectIds = orderItems.stream()
                .map(OrderItem::getObjectId)
                .distinct()
                .toList();

        return objectClient.getObjectListByIds(objectIds);
    }

}