package kr.co.loopz.order.domain;

import jakarta.persistence.*;
import kr.co.loopz.order.domain.enums.PaymentMethod;
import kr.co.loopz.order.dto.request.OrderRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
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

    @Column(nullable = false)
    private String addressId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(length = 500)
    private String deliveryRequest;

    @Column(unique = true)
    private String orderNumber;

    /**
     * 주문 생성 팩토리 메서드
     * @param request 주문 요청 정보 - addressId, paymentMethod, deliveryRequest
     * @param userId 주문을 요청한 사용자 ID
     * @return 주문 entity
     */
    public static Order createOrder(OrderRequest request, String userId) {
        return Order.builder()
                .userId(userId)
                .addressId(request.addressId())
                .paymentMethod(request.paymentMethod())
                .deliveryRequest(request.deliveryRequest())
                .build();
    }


    @Builder(access = PRIVATE)
    private Order(String userId,
                 String addressId,
                 PaymentMethod paymentMethod,
                 String deliveryRequest) {
        this.orderId = UUID.randomUUID().toString();
        this.orderNumber = createOrderNumber();

        this.userId = userId;
        this.addressId = addressId;
        this.paymentMethod = paymentMethod;
        this.deliveryRequest = deliveryRequest;
    }

    private String createOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%07d", new Random().nextInt(10_000_000));
        return datePart + "-" + randomPart;
    }



}
