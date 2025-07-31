package kr.co.loopz.payment.saga.command;

public record ChangeOrderStatusCommand(
        String orderId
) {
}
