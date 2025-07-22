package kr.co.loopz.payment.exception;

import kr.co.loopz.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum PaymentErrorCode implements ErrorCode {


    WEBHOOK_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "웹훅 검증에 실패했습니다."),
    PAYMENT_CLIENT_FAILED(HttpStatus.BAD_REQUEST, "결제 클라이언트 요청에 실패했습니다. paymentId가 올바른지 확인해주세요."),
    PAYMENT_CHANNEL_NOT_LIVE(HttpStatus.BAD_REQUEST, "결제 채널이 LIVE 상태가 아닙니다. 결제 채널을 확인해주세요."),
    PAYMENT_IS_NOT_PAID(HttpStatus.BAD_REQUEST, "결제가 완료되지 않았습니다. 결제 상태를 확인해주세요."),
    PAYMENT_PARSING_FAILED(HttpStatus.BAD_REQUEST, "결제 정보 파싱에 실패했습니다. 결제 정보를 확인해주세요."),
    PAYMENT_CALCULATION_FAILED(HttpStatus.BAD_REQUEST, "결제 금액 계산이 일치하지 않습니다. 결제 금액과 주문 금액을 확인해주세요."),
    PAYMENT_ITEMS_MISMATCH(HttpStatus.BAD_REQUEST, "결제된 상품 정보가 주문 정보와 일치하지 않습니다."),
    ORDER_SERVICE_CLIENT_FAILED(HttpStatus.BAD_REQUEST, "주문 서비스 클라이언트 요청에 실패했습니다. 주문 ID가 올바른지 확인해주세요."),
    PRODUCT_SERVICE_CLIENT_FAILED(HttpStatus.BAD_REQUEST, "상품 서비스 클라이언트 요청에 실패했습니다. 상품 ID가 올바른지 확인해주세요."),
    PAYMENT_USER_ID_MISMATCH(HttpStatus.BAD_REQUEST, "결제 사용자 ID가 일치하지 않습니다. 결제 정보와 사용자 정보를 확인해주세요.");



    private final HttpStatus httpStatus;
    private final String message;

}
