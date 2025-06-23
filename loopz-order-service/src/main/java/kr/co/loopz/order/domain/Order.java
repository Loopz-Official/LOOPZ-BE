package kr.co.loopz.order.domain;

import jakarta.persistence.*;
import kr.co.loopz.order.domain.enums.OrderStatus;
import kr.co.loopz.order.domain.enums.PaymentMethod;
import lombok.*;

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@AllArgsConstructor
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

    @Column
    private String cartId;

    @Column(nullable = false)
    private String addressId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;


    @Builder
    public Order(String userId,
                 String productId,
                 String cartId,
                 String addressId,
                 OrderStatus status,
                 PaymentMethod paymentMethod) {
        this.orderId = UUID.randomUUID().toString();
        this.userId = userId;
        this.productId = productId;
        this.cartId = cartId;
        this.addressId = addressId;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

}
