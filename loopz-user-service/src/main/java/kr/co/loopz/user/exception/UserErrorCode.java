package kr.co.loopz.user.exception;

import kr.co.loopz.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "사용중인 닉네임 입니다."),
    NICKNAME_INVALID(HttpStatus.BAD_REQUEST, "적절하지 않은 닉네임 입니다."),

    OBJECT_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 objectId입니다."),
    USER_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    NEED_LOGIN(HttpStatus.BAD_REQUEST, "로그인이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
