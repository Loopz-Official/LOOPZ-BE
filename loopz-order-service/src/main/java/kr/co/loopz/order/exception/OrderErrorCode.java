package kr.co.loopz.order.exception;

import kr.co.loopz.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum OrderErrorCode implements ErrorCode {

    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "주소가 존재하지 않습니다."),
    OBJECT_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 상품을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;

}
