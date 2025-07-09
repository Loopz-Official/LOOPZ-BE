package kr.co.loopz.search.exception;

import kr.co.loopz.common.exception.CustomException;
import kr.co.loopz.common.exception.ErrorCode;

public class SearchException extends CustomException {

    public SearchException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SearchException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
