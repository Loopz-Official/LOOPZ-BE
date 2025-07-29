package kr.co.loopz.payment.saga.event;

import kr.co.loopz.payment.dto.response.PurchasedObjectResponse;

import java.util.List;

public record PaymentCompleteEvent(
        String paymentId,
        String userId,
        String orderId,
        List<PurchasedObjectResponse> objects
) {
}
