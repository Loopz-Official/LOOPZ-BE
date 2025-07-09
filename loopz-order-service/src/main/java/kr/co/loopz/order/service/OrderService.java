package kr.co.loopz.order.service;

import kr.co.loopz.order.client.ObjectClient;
import kr.co.loopz.order.client.UserClient;
import kr.co.loopz.order.converter.OrderConverter;
import kr.co.loopz.order.domain.Order;
import kr.co.loopz.order.domain.OrderItem;
import kr.co.loopz.order.dto.request.ObjectRequest;
import kr.co.loopz.order.dto.request.OrderRequest;
import kr.co.loopz.order.dto.response.InternalObjectResponse;
import kr.co.loopz.order.dto.response.OrderResponse;
import kr.co.loopz.order.exception.OrderException;
import kr.co.loopz.order.repository.OrderItemRepository;
import kr.co.loopz.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static kr.co.loopz.order.constants.OrderConstants.SHIPPING_FEE;
import static kr.co.loopz.order.exception.OrderErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final OrderConverter orderConverter;

    private final UserClient userClient;
    private final ObjectClient objectClient;

    /**
     * 결제전 PENDING 상태의 주문 생성
     * user service 호출(주소 확인), object service 호출(상품 정보 조회)
     * 재고를 차감 하지 않음, 카트 상품을 삭제 하지 않음
     * @param userId 사용자 UUID
     * @param request 주문 요청 정보
     * @return 생성된 주문 정보
     */
    @Transactional
    public OrderResponse createOrder(String userId, OrderRequest request) {

        // 주소 확인
        validateAddress(userId, request.addressId());
        // Object Service에 상품 정보 조회 요청
        List<InternalObjectResponse> currentObjects = requestToObjectService(request.objects());
        // 재고 확인
        checkStock(request.objects(), currentObjects);
        // 재고 차감, 카트 삭제는 결제 완료 후 진행
        // 주문 생성, 저장
        Order order = Order.createOrder(request, userId);
        orderRepository.save(order);
        // 주문 아이템 생성, 저장
        List<OrderItem> items = saveOrderItems(request.objects(), currentObjects, order);

        return orderConverter.toOrderResponse(order, items, currentObjects, SHIPPING_FEE);
    }

    private List<OrderItem> saveOrderItems(
            List<ObjectRequest> requestObjects,
            List<InternalObjectResponse> currentObjects,
            Order order
    ) {

        List<OrderItem> result = new ArrayList<>();

        for (ObjectRequest orderObject : requestObjects) {
            InternalObjectResponse currentObjectInfo = currentObjects.stream()
                    .filter(currentObject -> currentObject.objectId().equals(orderObject.objectId()))
                    .findFirst()
                    .orElseThrow(() -> new OrderException(OBJECT_ID_NOT_FOUND, "요청한 상품 ID가 존재하지 않습니다: " + orderObject.objectId()));

            OrderItem orderItem = OrderItem.createOrderItem(order.getOrderId(),
                                                            orderObject.objectId(),
                                                            orderObject.quantity(),
                                                            currentObjectInfo.objectPrice());
            orderItemRepository.save(orderItem);
            result.add(orderItem);
        }

        return result;
    }

    private List<InternalObjectResponse> requestToObjectService(List<ObjectRequest> requestObjects) {
        List<String> requestIds = requestObjects.stream()
                .map(ObjectRequest::objectId)
                .toList();
        try {
            return objectClient.getObjectListByIds(requestIds);
        } catch (Exception e) {
            throw new OrderException(OBJECT_ID_NOT_FOUND, "요청한 상품 ID가 존재하지 않습니다: " + requestIds);
        }
    }

    private void checkStock(List<ObjectRequest> requestObjects, List<InternalObjectResponse> currentObjects) {
        for (InternalObjectResponse currentObject : currentObjects) {
            String currentObjectId = currentObject.objectId();
            int requestedQuantity = findRequestQuantityFromRequest(requestObjects, currentObjectId);

            if (requestedQuantity > currentObject.stock()) {
                throw new OrderException(OUT_OF_STOCK, "상품 ID: " + currentObjectId + " 재고 부족: 현재 재고 " + currentObject.stock() + ", 요청 수량 " + requestedQuantity);
            }
        }
    }

    private int findRequestQuantityFromRequest(List<ObjectRequest> requestObjects, String currentObjectId) {
        return requestObjects.stream()
                .filter(requestObject -> requestObject.objectId().equals(currentObjectId))
                .findFirst()
                .orElseThrow(() -> new OrderException(OBJECT_ID_NOT_FOUND, "요청한 상품 ID가 존재하지 않습니다: " + currentObjectId))
                .quantity();
    }

    private void validateAddress(String userId, String addressId) {
        // User service에 요청 - 사용자 주소 존재 여부 확인
        if (!userClient.existsAddressByUserId(userId, addressId)) {
            throw new OrderException(INVALID_ADDRESS, "addressId:" + addressId);
        }
    }

}