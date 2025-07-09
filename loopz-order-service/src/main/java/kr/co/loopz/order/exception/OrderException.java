package kr.co.loopz.order.exception;

import kr.co.loopz.common.exception.CustomException;
import kr.co.loopz.common.exception.ErrorCode;

public class OrderException extends CustomException {

    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OrderException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
