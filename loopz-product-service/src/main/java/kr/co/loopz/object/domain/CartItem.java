package kr.co.loopz.object.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Table(name = "cart_object_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String cartId;

    @Column(nullable = false)
    private String objectId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean selected = false;

    @Builder
    public CartItem(String cartId,String objectId, int quantity, boolean selected) {
        this.cartId = cartId;
        this.objectId = objectId;
        this.quantity = quantity;
        this.selected = selected;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void updateSelected(boolean selected) {
        this.selected = selected;
    }
}

