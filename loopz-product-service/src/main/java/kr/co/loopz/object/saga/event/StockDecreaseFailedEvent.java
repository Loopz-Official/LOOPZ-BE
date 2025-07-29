package kr.co.loopz.object.saga.event;

public record StockDecreaseFailedEvent(
        String orderId,
        String reason
) {
}
