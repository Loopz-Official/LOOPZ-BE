package kr.co.loopz.order.service;

import kr.co.loopz.order.client.ProductClient;
import kr.co.loopz.order.client.UserClient;
import kr.co.loopz.order.converter.OrderConverter;
import kr.co.loopz.order.dto.request.CartOrderRequest;
import kr.co.loopz.order.dto.request.DeleteCartItemRequest;
import kr.co.loopz.order.dto.request.OrderRequest;
import kr.co.loopz.order.dto.response.*;
import kr.co.loopz.order.exception.OrderException;
import kr.co.loopz.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.loopz.order.domain.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static kr.co.loopz.order.exception.OrderErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderConverter orderConverter;

    @Transactional
    public OrderResponse orderSingle(String userId, String objectId, OrderRequest request) {

        // 주소 확인
        validateAddress(userId, request.addressId());

        // 상품 정보 조회+ 재고 확인
        ObjectResponse object = validateObject(objectId, request.quantity());

        Order order = Order.createSingleOrder(userId, objectId, request);
        orderRepository.save(order);

        // 주문 생성 후 재고 감소
        productClient.decreaseStock(objectId, request.quantity());

        ObjectResponse orderItem = createOrderItem(object, request.quantity());

        int shippingFee = 3000;
        int totalProductPrice = orderItem.totalPrice();
        int totalPayment = totalProductPrice + shippingFee;

        return orderConverter.toOrderResponse(
                order,
                List.of(orderItem),
                shippingFee,
                totalProductPrice,
                totalPayment
        );
    }

    @Transactional
    public OrderResponse orderCart(String userId, CartOrderRequest request) {

        // 주소 확인
        validateAddress(userId, request.addressId());

        // 장바구니 리스트 조회
        CartWithQuantityResponse cartResponse = productClient.getCartByUserId(userId);
        List<CartItemResponse> cartItems = cartResponse.items();

        List<ObjectResponse> orderItems = new ArrayList<>();
        int totalProductPrice = 0;

        // 각 카트 상품별 주문
        for (String objectId : request.objectIds()){

            // objectId에 해당하는 카트 상품 찾기 (수량 포함)
            CartItemResponse cartItemResponse = cartItems.stream()
                    .filter(ci -> ci.object().objectId().equals(objectId))
                    .findFirst()
                    .orElseThrow(() -> new OrderException(CART_ITEM_NOT_FOUND, "카트에 없는 상품입니다. objectId: " + objectId));

            int quantity = cartItemResponse.quantity();

            // 상품 정보 조회 + 재고 확인
            ObjectResponse object = validateObject(objectId, quantity);

            ObjectResponse orderItem = createOrderItem(object, quantity);


            orderItems.add(orderItem);
            totalProductPrice += orderItem.totalPrice();
        }

        // 주문 생성
        Order order = Order.createMultiOrder(userId, request);
        orderRepository.save(order);

        // 주문 생성 후 재고 감소
        for (String objectId : request.objectIds()) {

            CartItemResponse cartItemResponse = cartItems.stream()
                    .filter(ci -> ci.object().objectId().equals(objectId))
                    .findFirst()
                    .get();

            int quantity = cartItemResponse.quantity();

            //재고 차감
            productClient.decreaseStock(objectId, quantity);

            // 카트 상품 삭제
            productClient.deleteCartItem(new DeleteCartItemRequest(userId, objectId));
        }

        int shippingFee = 3000;
        int totalPayment = totalProductPrice + shippingFee;

        return orderConverter.toOrderResponse(
                order,
                orderItems,
                shippingFee,
                totalProductPrice,
                totalPayment
        );
    }

    // 공통: 주소 검증
    private void validateAddress(String userId, String addressId) {
        if (!userClient.existsAddressByUserId(userId, addressId)) {
            throw new OrderException(INVALID_ADDRESS, "addressId:" + addressId);
        }
    }

    // 공통: 상품 조회 + 재고 검증
    private ObjectResponse validateObject(String objectId, int quantity) {
        ObjectResponse object = productClient.getObjectById(objectId);
        if (object == null) {
            throw new OrderException(OBJECT_ID_NOT_FOUND, "objectId:" + objectId);
        }

        int stock = productClient.getStockByObjectId(objectId);
        if (quantity > stock) {
            throw new OrderException(OUT_OF_STOCK, "상품ID: " + objectId + " 재고 부족: 현재 재고 " + stock + ", 요청 수량 " + quantity);
        }

        return object;
    }

    // 공통: 주문 상품 생성
    private ObjectResponse createOrderItem(ObjectResponse object, int quantity) {
        return new ObjectResponse(
                object.objectId(),
                object.objectName(),
                object.imageUrl(),
                object.objectPrice(),
                quantity,
                object.objectPrice() * quantity
        );
    }
}