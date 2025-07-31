package kr.co.loopz.admin.exception;

import kr.co.loopz.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AdminErrorCode implements ErrorCode {

    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "주소가 존재하지 않습니다.");


    private final HttpStatus httpStatus;
    private final String message;

}
