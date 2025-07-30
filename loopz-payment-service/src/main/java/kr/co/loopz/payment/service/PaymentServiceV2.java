package kr.co.loopz.payment.service;

import kr.co.loopz.payment.converter.PaymentConverter;
import kr.co.loopz.payment.domain.PaymentEntity;
import kr.co.loopz.payment.dto.response.PaymentCompleteResponse;
import kr.co.loopz.payment.repository.PaymentRepository;
import kr.co.loopz.payment.saga.event.PaymentCompleteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceV2 {

    private final PaymentService paymentService;

    private final PaymentRepository paymentRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final PaymentConverter paymentConverter;


    /**
     * 최종적인 결제 성공 처리를 위해 사용됩니다.
     * 웹훅 수신 후 결제 정보를 주문과 비교해 검증하고 결제 정보를 DB에 기록, 재고 및 장바구니 데이터를 업데이트합니다.
     * V2 버전에서는 결제 완료 이벤트를 발행하고, 이후 SAGA Orchestrator가 재고 감소 및 주문 상태 변경을 처리합니다.
     * @param paymentId 결제 ID
     */
    @Transactional
    public void syncPaymentAndUpdateStock(String paymentId) {

        if (paymentRepository.existsByPaymentId(paymentId)) {
            log.info("이미 처리된 결제 웹훅입니다. 중복 처리를 방지합니다. 결제 ID: {}", paymentId);
            return;
        }

        PaymentCompleteResponse response = paymentService.syncPayment(paymentId, null);
        log.info("웹훅 수신 후 결제 정보 동기화 성공, 결제 ID: {} 재고 및 장바구니 데이터 싱크 처리 시작", paymentId);

        PaymentEntity payment = PaymentEntity.createPayment(response);
        paymentRepository.save(payment);

        PaymentCompleteEvent event = paymentConverter.toPaymentCompleteEvent(response);
        kafkaTemplate.send("payment-completed-topic", event);

        payment.makeWebhookVerified();
    }


}
