package kr.co.loopz.admin.exception;

import kr.co.loopz.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AdminErrorCode implements ErrorCode {

    DUPLICATE_OBJECT_NAME(HttpStatus.BAD_REQUEST, "상품이 존재합니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "invalid request"),
    CANNOT_FIND_OBJECT(HttpStatus.BAD_REQUEST, "상품을 찾을 수 없습니다." );


    private final HttpStatus httpStatus;
    private final String message;

}
