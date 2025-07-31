package kr.co.loopz.search.exception;

import kr.co.loopz.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SearchErrorCode implements ErrorCode {

    USER_ID_NOT_MATCH(HttpStatus.BAD_REQUEST, "해당 사용자의 검색어가 아닙니다."),
    SEARCH_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 검색어를 찾을 수 없습니다."),
    SEARCH_HISTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, " 최근 검색어가 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;

}
