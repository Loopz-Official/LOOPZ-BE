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
    NEED_LOGIN(HttpStatus.BAD_REQUEST, "로그인이 필요합니다."),

    FIRST_ADDRESS_MUST_BE_DEFAULT(HttpStatus.BAD_REQUEST, "첫 배송지는 기본 배송지로 설정해야 합니다."),
    ADDRESS_EXISTS(HttpStatus.BAD_REQUEST, "이미 등록된 배송지입니다."),
    ALREADY_HAS_DEFAULT_ADDRESS(HttpStatus.BAD_REQUEST, "기본 배송지는 1개만 설정 가능합니다."),
    ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 주소입니다."),
    NEED_DEFAULT_ADDRESS(HttpStatus.BAD_REQUEST, "기본배송지 지정이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
