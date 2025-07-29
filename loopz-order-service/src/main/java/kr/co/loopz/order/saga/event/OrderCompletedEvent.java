package kr.co.loopz.order.saga.event;

public record OrderCompletedEvent(
        String orderId
) {
}
