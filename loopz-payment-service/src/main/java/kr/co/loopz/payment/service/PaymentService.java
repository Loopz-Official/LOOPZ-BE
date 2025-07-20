package kr.co.loopz.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import kr.co.loopz.payment.client.OrderServiceClient;
import kr.co.loopz.payment.converter.PaymentConverter;
import kr.co.loopz.payment.dto.response.InternalOrderResponse;
import kr.co.loopz.payment.dto.response.PaymentCompleteResponse;
import kr.co.loopz.payment.dto.response.PortOneCustomData;
import kr.co.loopz.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static kr.co.loopz.payment.exception.PaymentErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentClient paymentClient;
    private final OrderServiceClient orderServiceClient;

    private final ObjectMapper objectMapper;
    private final PaymentConverter paymentConverter;

    public PaymentCompleteResponse syncPayment(String paymentId) {

        Payment actualPayment = requestToPortOne(paymentId);
        PaidPayment paidPayment = checkPaidPayment(paymentId, actualPayment);

        // 테스트 서버에서는 검증하지 않음, PROD 환경에서 주석 제거
        //        checkPaymentChannel(payment);
        PortOneCustomData customData = parsePortoneCustomData(paidPayment);

        InternalOrderResponse orderResponse = requestToOrderService(customData.orderId());
        checkPaymentCalculation(customData, orderResponse);

        log.info("결제 성공 - {}, 주문 정보 : {}", actualPayment, customData);

        return paymentConverter.toPaymentCompleteResponse(customData, orderResponse, paidPayment.getMethod());
    }


    public void syncPaymentAndUpdateStock(String paymentId) {
        syncPayment(paymentId);
        log.info("웹훅 수신 후 결제 정보 동기화 성공, 결제 ID: {} 재고 및 장바구니 데이터 싱크 처리 시작", paymentId);
        //        requestToProductService(List<PurchasedProduct> purchasedProducts);
    }


    /**
     * 포트원 결제 클라이언트에 paymentId로 요청을 보내 결제 정보를 가져옵니다.
     * @param paymentId 결제 ID
     * @return 결제 정보
     */
    private Payment requestToPortOne(String paymentId) {
        Payment actualPayment;
        try {
            CompletableFuture<Payment> payment = paymentClient.getPayment(paymentId);
            actualPayment = payment.get();
        } catch (Exception e) {
            log.error("Failed to get payment for paymentId={}", paymentId, e);
            throw new PaymentException(PAYMENT_CLIENT_FAILED);
        }
        return actualPayment;
    }


    /**
     * 결제 정보가 PaidPayment 타입인지 확인합니다. 다형성을 사용한 검증 메서드입니다.
     * 결제가 완료된 상태인지 확인하고, 그렇지 않으면 예외를 발생시킵니다.
     * @param paymentId 결제 ID
     * @param actualPayment 실제 결제 정보
     * @return PaidPayment 객체
     */
    private PaidPayment checkPaidPayment(String paymentId, Payment actualPayment) {
        if (!(actualPayment instanceof PaidPayment paidPayment)) {
            log.error("결제 정보가 유효하지 않습니다. paymentId: {}, actual payment: {}", paymentId, actualPayment);
            throw new PaymentException(PAYMENT_IS_NOT_PAID, paymentId + " : paymentID에 대한 결제가 완료 되지 않았습니다. 결제 상태를 확인해주세요.");
        }
        return paidPayment;
    }


    private PortOneCustomData parsePortoneCustomData(PaidPayment payment) {

        String customData = payment.getCustomData();
        PortOneCustomData portOneCustomData;

        try {
            portOneCustomData = objectMapper.readValue(customData, PortOneCustomData.class);

        } catch (JsonProcessingException e) {
            log.error("결제 정보 파싱에 실패했습니다. paymentId: {}, customData: {}", payment.getId(), customData, e);
            throw new PaymentException(PAYMENT_PARSING_FAILED, "결제 정보 파싱에 실패했습니다. 상품 정보 스키마가 일치하지 않습니다.");
        }

        return portOneCustomData;
    }


    /**
     * 결제 채널이 LIVE 상태인지 확인합니다. 테스트 환경인지 확인하기 위한 메서드입니다.
     * @param payment 결제 정보
     */
    private void checkPaymentChannel(PaidPayment payment) {
        if (!payment.getChannel().getType().getValue().equals("LIVE")) {
            log.error("Payment channel is not live: {}", payment.getChannel().getType());
            throw new PaymentException(PAYMENT_CHANNEL_NOT_LIVE);
        }
    }


    /**
     * 주문 서비스에 주문 ID로 요청을 보내 주문 정보를 가져옵니다.
     * @param orderId 주문 ID
     * @return 주문 정보
     */
    private InternalOrderResponse requestToOrderService(String orderId) {
        try {
            return orderServiceClient.getOrderById(orderId);
        } catch (Exception e) {
            log.error("Failed to get order for orderId={}", orderId, e);
            throw new PaymentException(ORDER_SERVICE_CLIENT_FAILED);
        }
    }


    private void checkPaymentCalculation(PortOneCustomData customData, InternalOrderResponse orderResponse) {

    }


}
