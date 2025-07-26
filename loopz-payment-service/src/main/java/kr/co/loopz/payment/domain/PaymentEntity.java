package kr.co.loopz.payment.domain;

import jakarta.persistence.*;
import kr.co.loopz.common.domain.BaseTimeEntity;
import kr.co.loopz.payment.dto.response.PaymentCompleteResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    // 정합성을 맞추기 위해 내부 UUID가 아닌 포트원에서 제공한 paymentId를 사용
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

    private boolean webhookVerified;

    private LocalDateTime canceledAt;
    private String cancelReason;

    /**
     * 결제 성공 웹훅을 수신하고 기록할때 사용하는 팩토리 메서드입니다.
     * @param response 결제 성공 후 검증 성공 응답
     * @return PaymentEntity
     */
    public static PaymentEntity createPayment(PaymentCompleteResponse response) {

        String method = response.paidPayment().getMethod() != null
                ? response.paidPayment().getMethod().getClass().getSimpleName()
                : null;

        return PaymentEntity.builder()
                .paymentId(response.paidPayment().getId())
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
            String paymentId,
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
        this.paymentId = paymentId;
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


    public void makeWebhookVerified() {
        this.webhookVerified = true;
    }

}
