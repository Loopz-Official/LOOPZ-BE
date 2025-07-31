package kr.co.loopz.payment.saga.event;

import java.util.List;

public record PaymentCompleteEvent(
        String paymentId,
        String userId,
        String orderId,
        List<KafkaPurchasedObject> objects
) {
}
