package kr.co.loopz.admin.exception;

import kr.co.loopz.common.exception.CustomException;
import kr.co.loopz.common.exception.ErrorCode;

public class AdminException extends CustomException {

    public AdminException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AdminException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
