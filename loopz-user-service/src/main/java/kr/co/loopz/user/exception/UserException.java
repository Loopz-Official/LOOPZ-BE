package kr.co.loopz.user.exception;

import kr.co.loopz.common.exception.CustomException;
import kr.co.loopz.common.exception.ErrorCode;

public class UserException extends CustomException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
