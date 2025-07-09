package kr.co.loopz.authorization.exception;

import kr.co.loopz.common.exception.CustomException;

public class TokenException extends CustomException {

    public TokenException(SecurityErrorCode errorCode) {
        super(errorCode);
    }

    public TokenException(SecurityErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
