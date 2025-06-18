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
    USER_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 사용자가 존재하지 않습니다."),

    CART_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자의 장바구니가 존재하지 않습니다."),
    QUANTITY_EXCEEDS_STOCK(HttpStatus.BAD_REQUEST, "입력 수량이 입고 수량을 초과합니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "수량은 0 이상이어야 합니다."),
    CART_LEAST_ONE(HttpStatus.BAD_REQUEST, "장바구니 수량은 1 이상이어야 합니다."),
    CART_LIMIT_EXCEEDS(HttpStatus.BAD_REQUEST, "장바구니에는 100개의 상품만 추가 가능합니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "장바구니에 존재하지 않는 상품입니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
