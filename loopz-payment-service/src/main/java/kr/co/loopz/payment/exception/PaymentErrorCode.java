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
    ORDER_SERVICE_CLIENT_FAILED(HttpStatus.BAD_REQUEST, "주문 서비스 클라이언트 요청에 실패했습니다. 주문 ID가 올바른지 확인해주세요.");



    private final HttpStatus httpStatus;
    private final String message;

}
