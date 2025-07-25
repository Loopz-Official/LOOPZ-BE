package kr.co.loopz.payment.apiExternal;

import io.portone.sdk.server.errors.WebhookVerificationException;
import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookTransaction;
import io.portone.sdk.server.webhook.WebhookVerifier;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.loopz.payment.dto.request.PaymentCompleteRequest;
import kr.co.loopz.payment.dto.response.PaymentCompleteResponse;
import kr.co.loopz.payment.exception.PaymentException;
import kr.co.loopz.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static kr.co.loopz.payment.exception.PaymentErrorCode.WEBHOOK_VERIFICATION_FAILED;

@RestController
@RequestMapping("/payment/v1/")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final WebhookVerifier webhookVerifier;


    /**
     * 클라이언트 결제 완료 요청을 처리합니다. 결제 완료 상태인지 확인하고 응답합니다.
     * 이 요청을 신뢰하지 않고 웹훅을 신뢰하기 위해 재고를 감소하지 않고, 주문을 확정하지 않습니다.
     * @param paymentCompleteRequest 결제 완료 payment id
     * @param currentUser 현재 인증된 사용자 정보
     * @return 결제 완료 응답
     */
    @PostMapping("/complete")
    public ResponseEntity<PaymentCompleteResponse> completePayment(
            @RequestBody @Valid PaymentCompleteRequest paymentCompleteRequest,
            @AuthenticationPrincipal User currentUser
    ) {

        String userId = currentUser.getUsername();
        String paymentId = paymentCompleteRequest.paymentId();
        log.debug("Payment completion request: paymentId={}, userId={}", paymentId, userId);

        PaymentCompleteResponse response = paymentService.syncPayment(paymentId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 포트원 웹훅을 처리합니다. 포트원쪽 요청을 신뢰하며 재고를 감소하고 주문을 확정합니다. 사용자 장바구니를 조회해 상품을 삭제합니다.
     * @param webhookId 웹훅 ID
     * @param webhookTimestamp 웹훅 타임스탬프
     * @param webhookSignature 웹훅 서명
     * @return void
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            HttpServletRequest request,
            @RequestHeader("webhook-id") String webhookId,
            @RequestHeader("webhook-timestamp") String webhookTimestamp,
            @RequestHeader("webhook-signature") String webhookSignature
    ) {

        String rawBody;
        try (ServletInputStream inputStream = request.getInputStream()) {
            rawBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read raw body", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.debug("Received webhook: body={}, webhookId={}, webhookTimestamp={}, webhookSignature={}",
                  rawBody, webhookId, webhookTimestamp, webhookSignature);
        Webhook verifiedWebhook = verifyWebhook(rawBody, webhookId, webhookTimestamp, webhookSignature);
        log.debug("verified webhook type : {}", verifiedWebhook.getClass().getTypeName());
        WebhookTransaction transaction = getWebhookTransaction(rawBody, webhookId, webhookTimestamp, webhookSignature, verifiedWebhook);
        log.debug("Webhook is a transaction: {}", transaction);

        paymentService.syncPaymentAndUpdateStock(transaction.getData().getPaymentId());
        return ResponseEntity.status(HttpStatus.OK).body("Webhook processed successfully");
    }



    private Webhook verifyWebhook(String body, String webhookId, String webhookTimestamp, String webhookSignature) {
        Webhook verifiedWebhook;

        try {
            verifiedWebhook = webhookVerifier.verify(body, webhookId, webhookSignature ,webhookTimestamp);
        } catch (WebhookVerificationException e) {
            log.error("Webhook verification failed", e);
            throw new PaymentException(WEBHOOK_VERIFICATION_FAILED);
        }

        return verifiedWebhook;
    }



    private WebhookTransaction getWebhookTransaction(String body, String webhookId, String webhookTimestamp, String webhookSignature, Webhook verifiedWebhook) {

        if (!(verifiedWebhook instanceof WebhookTransaction transaction)) {
            log.error("Webhook is not a transaction: body={}, webhookId={}, webhookTimestamp={}, webhookSignature={}",
                      body, webhookId, webhookTimestamp, webhookSignature);
            throw new PaymentException(WEBHOOK_VERIFICATION_FAILED);
        }
        return transaction;
    }


}
