package kr.co.loopz.payment.domain;

import jakarta.persistence.*;
import kr.co.loopz.common.domain.BaseTimeEntity;
import kr.co.loopz.payment.dto.response.PaymentCompleteResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "payment")
public class PaymentEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String userId;

    private String paymentMethod;

    @Column(columnDefinition = "text")
    private String transactionId;
    private String orderName;
    private Long paymentAmount;
    private String currency;
    @Column(columnDefinition = "text")
    private String pgTxId;
    @Column(columnDefinition = "text")
    private String receiptUrl;

    private LocalDateTime canceledAt;
    private String cancelReason;

    public static PaymentEntity createPayment(PaymentCompleteResponse response) {

        String method = response.paidPayment().getMethod() != null
                ? response.paidPayment().getMethod().getClass().getSimpleName()
                : null;

        return PaymentEntity.builder()
                .orderId(response.orderId())
                .userId(response.userId())
                .paymentMethod(method)
                .transactionId(response.paidPayment().getTransactionId())
                .orderName(response.paidPayment().getOrderName())
                .paymentAmount(response.paidPayment().getAmount().getTotal())
                .currency(response.paidPayment().getCurrency().getValue())
                .pgTxId(response.paidPayment().getPgTxId())
                .receiptUrl(response.paidPayment().getReceiptUrl())
                .build();
    }

    @Builder(access = PRIVATE)
    private PaymentEntity(
            String orderId,
            String userId,
            String paymentMethod,
            String transactionId,
            String orderName,
            Long paymentAmount,
            String currency,
            String pgTxId,
            String receiptUrl
    ) {
        this.paymentId = UUID.randomUUID().toString();

        this.orderId = orderId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.orderName = orderName;
        this.paymentAmount = paymentAmount;
        this.currency = currency;
        this.pgTxId = pgTxId;
        this.receiptUrl = receiptUrl;
    }


}
