package kr.co.loopz.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@AllArgsConstructor
@Table(name = "order_object_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column( nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String objectId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Long purchasePrice;


    public static OrderItem createOrderItem(String orderId, String objectId, int quantity, Long purchasePrice) {
        return OrderItem.builder()
                .orderId(orderId)
                .objectId(objectId)
                .quantity(quantity)
                .purchasePrice(purchasePrice)
                .build();
    }

    @Builder(access = PRIVATE)
    private OrderItem(String orderId, String objectId, int quantity, Long purchasePrice) {
        this.orderId = orderId;
        this.objectId = objectId;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

}
