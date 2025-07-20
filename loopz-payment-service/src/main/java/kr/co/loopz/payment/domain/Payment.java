package kr.co.loopz.payment.domain;

import jakarta.persistence.*;
import kr.co.loopz.common.domain.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "payment")
public class Payment extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String userId;

    private String paymentMethod;



}
