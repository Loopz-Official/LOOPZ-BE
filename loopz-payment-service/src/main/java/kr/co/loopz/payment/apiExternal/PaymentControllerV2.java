package kr.co.loopz.payment.apiExternal;

import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookTransaction;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.loopz.payment.service.PaymentServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/payment/v2/")
@RequiredArgsConstructor
@Slf4j
public class PaymentControllerV2 {

    private final PaymentController paymentController;
    private final PaymentServiceV2 paymentServiceV2;


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
        Webhook verifiedWebhook = paymentController.verifyWebhook(rawBody, webhookId, webhookTimestamp, webhookSignature);
        log.debug("verified webhook type : {}", verifiedWebhook.getClass().getTypeName());
        WebhookTransaction transaction = paymentController.getWebhookTransaction(rawBody, webhookId, webhookTimestamp, webhookSignature, verifiedWebhook);
        log.debug("Webhook is a transaction: {}", transaction);

        paymentServiceV2.syncPaymentAndUpdateStock(transaction.getData().getPaymentId());
        return ResponseEntity.status(HttpStatus.OK).body("Webhook processed successfully");
    }




}
