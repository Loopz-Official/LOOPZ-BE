package kr.co.loopz.payment.exception;

import kr.co.loopz.common.exception.CustomException;
import kr.co.loopz.common.exception.ErrorCode;

public class PaymentException extends CustomException {
    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PaymentException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
