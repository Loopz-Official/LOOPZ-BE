package kr.co.loopz.object.Exception;

import kr.co.loopz.common.exception.CustomException;
import kr.co.loopz.common.exception.ErrorCode;

public class ObjectException extends CustomException {

    public ObjectException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ObjectException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
