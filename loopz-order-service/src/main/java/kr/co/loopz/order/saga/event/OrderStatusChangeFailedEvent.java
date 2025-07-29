package kr.co.loopz.order.saga.event;

public record OrderStatusChangeFailedEvent(
        String orderId,
        String reason
) {
}
