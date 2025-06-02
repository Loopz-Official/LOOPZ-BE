package kr.co.loopz.authentication.exception;

import kr.co.loopz.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Getter
public enum AuthenticationErrorCode implements ErrorCode {

    INVALID_CREDENTIALS(UNAUTHORIZED, "잘못된 인증 정보입니다."),
    ACCOUNT_LOCKED(FORBIDDEN, "계정이 잠겼습니다. 관리자에게 문의하세요."),
    ACCOUNT_DISABLED(FORBIDDEN, "계정이 비활성화되었습니다. 관리자에게 문의하세요.");

    private final HttpStatus httpStatus;
    private final String message;

}
