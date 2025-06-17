package kr.co.loopz.object.domain;

import jakarta.persistence.*;
import kr.co.loopz.object.domain.enums.Keyword;
import kr.co.loopz.object.domain.enums.ObjectSize;
import kr.co.loopz.object.domain.enums.ObjectType;
import lombok.*;

import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cartId;

    @Column(nullable = false)
    private String userId;

    @Builder(access = AccessLevel.PRIVATE)
    public Cart(String userId) {
        this.cartId = UUID.randomUUID().toString();
        this.userId = userId;
    }
}
