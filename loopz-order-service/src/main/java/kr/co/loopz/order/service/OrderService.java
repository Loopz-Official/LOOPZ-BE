package kr.co.loopz.order.service;

import kr.co.loopz.order.client.ProductClient;
import kr.co.loopz.order.client.UserClient;
import kr.co.loopz.order.converter.OrderConverter;
import kr.co.loopz.order.dto.request.OrderRequest;
import kr.co.loopz.order.dto.response.ObjectResponse;
import kr.co.loopz.order.dto.response.OrderResponse;
import kr.co.loopz.order.exception.OrderException;
import kr.co.loopz.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.loopz.order.domain.Order;

import java.util.List;

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
        if (!userClient.existsAddressByUserId(userId, request.addressId())) {
            throw new OrderException(INVALID_ADDRESS, "addressId:"+request.addressId());
        }

        // 상품 정보 조회
        ObjectResponse object = productClient.getObjectById(objectId);
        if (object == null) {
            throw new OrderException(OBJECT_ID_NOT_FOUND,"objectId:"+objectId);
        }

        // 재고 확인
        int stock = productClient.getStockByObjectId(objectId);
        if (request.quantity() > stock) {
            throw new OrderException(OUT_OF_STOCK, "재고 부족: 현재 재고 " + stock + ", 요청 수량 " + request.quantity());
        }

        Order order = Order.createSingleOrder(userId, objectId, request);
        orderRepository.save(order);

        // 주문 생성 후 재고 감소
        productClient.decreaseStock(objectId,request.quantity());

        ObjectResponse orderItem = new ObjectResponse(
                object.objectName(),
                object.imageUrl(),
                object.objectPrice(),
                request.quantity(),
                object.objectPrice() * request.quantity()
        );

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
}