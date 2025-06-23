package kr.co.loopz.order.domain;

import jakarta.persistence.*;
import kr.co.loopz.order.domain.enums.OrderStatus;
import kr.co.loopz.order.domain.enums.PaymentMethod;
import kr.co.loopz.order.dto.request.CartOrderRequest;
import kr.co.loopz.order.dto.request.OrderRequest;
import lombok.*;

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String userId;

    @Column
    private String productId;

    @Column(nullable = false)
    private String addressId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(length = 500)
    private String deliveryRequest;


    @Builder
    public Order(String userId,
                 String productId,
                 String addressId,
                 OrderStatus status,
                 PaymentMethod paymentMethod,
                 String deliveryRequest) {
        this.orderId = UUID.randomUUID().toString();
        this.userId = userId;
        this.productId = productId;
        this.addressId = addressId;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.deliveryRequest = deliveryRequest;
    }

    public static Order createSingleOrder(String userId, String productId, OrderRequest request) {
        return Order.builder()
                .userId(userId)
                .productId(productId)
                .addressId(request.addressId())
                .status(OrderStatus.ORDERED)
                .paymentMethod(request.paymentMethod())
                .deliveryRequest(request.deliveryRequest())
                .build();
    }

    public static Order createMultiOrder(String userId, CartOrderRequest request) {
        return Order.builder()
                .userId(userId)
                .productId(null)
                .addressId(request.addressId())
                .status(OrderStatus.ORDERED)
                .paymentMethod(request.paymentMethod())
                .deliveryRequest(request.deliveryRequest())
                .build();
    }


}
