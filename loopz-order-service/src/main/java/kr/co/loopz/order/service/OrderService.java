package kr.co.loopz.order.service;

import kr.co.loopz.order.client.ObjectClient;
import kr.co.loopz.order.client.UserClient;
import kr.co.loopz.order.constants.OrderConstants;
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

import static kr.co.loopz.order.constants.OrderConstants.*;
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

        return orderConverter.toOrderResponse(order, items, request.paymentMethod(), SHIPPING_FEE);
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



//
//    @Transactional
//    public OrderResponse orderSingle(String userId, String objectId, OrderRequest request) {
//
//        // 주소 확인
//        validateAddress(userId, request.addressId());
//
//        // 상품 정보 조회+ 재고 확인
//        ObjectResponse object = validateObject(objectId, request.quantity());
//
//        // 약관 동의 여부 확인
//        if (!request.agreedToTerms()) {
//            throw new OrderException(ORDER_TERM_NOT_AGREED);
//        }
//
//
//        Order order = Order.createSingleOrder(userId, request);
//        orderRepository.save(order);
//
//        // OrderItem 생성 및 저장
//        OrderItem orderItem = OrderItem.builder()
//                .orderId(order.getOrderId())
//                .objectId(objectId)
//                .quantity(request.quantity())
//                .price(object.purchasePrice() * (long) request.quantity())
//                .build();
//        orderItemRepository.save(orderItem);
//
//        // 주문 생성 후 재고 감소
//        productClient.decreaseStock(objectId, request.quantity());
//
//        ObjectResponse orderItemDto = createOrderItem(object, request.quantity());
//
//        int shippingFee = 3000;
//        int totalProductPrice = orderItemDto.totalPrice();
//        int totalPayment = totalProductPrice + shippingFee;
//
//        return orderConverter.toOrderResponse(
//                order,
//                List.of(orderItemDto),
//                shippingFee,
//                totalProductPrice,
//                totalPayment
//        );
//    }
//
//    @Transactional
//    public OrderResponse orderCart(String userId, CartOrderRequest request) {
//
//        // 주소 확인
//        validateAddress(userId, request.addressId());
//
//        // 약관 동의 여부 확인
//        if (!request.agreedToTerms()) {
//            throw new OrderException(ORDER_TERM_NOT_AGREED);
//        }
//
//        // 장바구니 리스트 조회
//        CartWithQuantityResponse cartResponse = productClient.getCartByUserId(userId);
//        List<CartItemResponse> cartItems = cartResponse.items();
//
//        List<ObjectResponse> orderItems = new ArrayList<>();
//        int totalProductPrice = 0;
//
//        Order order = Order.createMultiOrder(userId, request);
//        orderRepository.save(order);
//
//        // 각 카트 상품별 주문
//        for (String objectId : request.objectIds()){
//
//            // objectId에 해당하는 카트 상품 찾기 (수량 포함)
//            CartItemResponse cartItemResponse = cartItems.stream()
//                    .filter(ci -> ci.object().objectId().equals(objectId))
//                    .findFirst()
//                    .orElseThrow(() -> new OrderException(CART_ITEM_NOT_FOUND, "카트에 없는 상품입니다. objectId: " + objectId));
//
//            int quantity = cartItemResponse.quantity();
//
//            // 상품 정보 조회 + 재고 확인
//            ObjectResponse object = validateObject(objectId, quantity);
//
//            // OrderItem 엔티티 저장
//            OrderItem orderItem = OrderItem.builder()
//                    .orderId(order.getOrderId())
//                    .objectId(objectId)
//                    .quantity(quantity)
//                    .price(object.purchasePrice() * (long) quantity)
//                    .build();
//            orderItemRepository.save(orderItem);
//
//            ObjectResponse orderItemDto = createOrderItem(object, quantity);
//
//
//            orderItems.add(orderItemDto);
//            totalProductPrice += orderItemDto.totalPrice();
//        }
//
//        // 주문 생성 후 재고 감소
//        for (String objectId : request.objectIds()) {
//
//            CartItemResponse cartItemResponse = cartItems.stream()
//                    .filter(ci -> ci.object().objectId().equals(objectId))
//                    .findFirst()
//                    .get();
//
//            int quantity = cartItemResponse.quantity();
//
//            //재고 차감
//            productClient.decreaseStock(objectId, quantity);
//
//            // 카트 상품 삭제
//            productClient.deleteCartItem(new DeleteCartItemRequest(userId, objectId));
//        }
//
//        int shippingFee = 3000;
//        int totalPayment = totalProductPrice + shippingFee;
//
//        return orderConverter.toOrderResponse(
//                order,
//                orderItems,
//                shippingFee,
//                totalProductPrice,
//                totalPayment
//        );
//    }

//    // 공통: 상품 조회 + 재고 검증
//    private ObjectResponse validateObject(String objectId, int quantity) {
//        ObjectResponse object = productClient.getObjectById(objectId);
//        if (object == null) {
//            throw new OrderException(OBJECT_ID_NOT_FOUND, "objectId:" + objectId);
//        }
//
//        int stock = productClient.getStockByObjectId(objectId);
//        if (quantity > stock) {
//            throw new OrderException(OUT_OF_STOCK, "상품ID: " + objectId + " 재고 부족: 현재 재고 " + stock + ", 요청 수량 " + quantity);
//        }
//
//        return object;
//    }

    private void validateAddress(String userId, String addressId) {
        // User service에 요청 - 사용자 주소 존재 여부 확인
        if (!userClient.existsAddressByUserId(userId, addressId)) {
            throw new OrderException(INVALID_ADDRESS, "addressId:" + addressId);
        }
    }

}