package kr.co.loopz.payment.saga.event;

public record StockDecreaseFailedEvent(
        String orderId,
        String reason
) {
}
