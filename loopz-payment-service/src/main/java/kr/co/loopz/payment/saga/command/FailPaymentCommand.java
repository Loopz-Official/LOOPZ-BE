package kr.co.loopz.payment.saga.command;

public record FailPaymentCommand(
        String orderId,
        String reason
) {
}
