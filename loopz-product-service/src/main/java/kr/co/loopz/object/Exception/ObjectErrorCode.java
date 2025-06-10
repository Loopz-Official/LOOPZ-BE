package kr.co.loopz.object.Exception;

import kr.co.loopz.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ObjectErrorCode implements ErrorCode {

    INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 정렬 기준입니다."),
    OBJECT_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 상품이 존재하지 않습니다."),
    USER_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 사용자가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
