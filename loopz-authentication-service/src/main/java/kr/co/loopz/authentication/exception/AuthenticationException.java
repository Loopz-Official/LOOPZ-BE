package kr.co.loopz.authentication.exception;

import kr.co.loopz.common.exception.CustomException;

public class AuthenticationException extends CustomException {

    public AuthenticationException(AuthenticationErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(AuthenticationErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
