package kr.co.loopz.exception;

public class TokenException extends CustomException {

    public TokenException(SecurityErrorCode errorCode) {
        super(errorCode);
    }

    public TokenException(SecurityErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
