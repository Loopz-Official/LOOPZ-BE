package kr.co.loopz.user.exception;

import kr.co.loopz.common.exception.CustomException;

public class UserException extends CustomException {

  public UserException(UserErrorCode errorCode) {
    super(errorCode);
  }

  public UserException(UserErrorCode errorCode, String message) {
    super(errorCode, message);
  }

}
