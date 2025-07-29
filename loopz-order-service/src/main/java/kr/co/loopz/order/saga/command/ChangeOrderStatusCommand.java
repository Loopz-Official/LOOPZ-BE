package kr.co.loopz.order.saga.command;

public record ChangeOrderStatusCommand(
        String orderId
) {
}
