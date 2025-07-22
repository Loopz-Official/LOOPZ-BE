package kr.co.loopz.payment.service;

import kr.co.loopz.payment.dto.response.InternalOrderResponse;
import kr.co.loopz.payment.dto.response.PortOneCustomData;
import kr.co.loopz.payment.dto.response.PurchasedItem;
import kr.co.loopz.payment.dto.response.PurchasedObjectResponse;
import kr.co.loopz.payment.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.co.loopz.payment.exception.PaymentErrorCode.PAYMENT_CALCULATION_FAILED;
import static kr.co.loopz.payment.exception.PaymentErrorCode.PAYMENT_ITEMS_MISMATCH;

@Service
@Slf4j
public class PaymentCalculationService {

    /**
     * 결제 금액과 주문 금액이 일치하는지 검증합니다.
     * 결제된 상품 정보와 주문 정보가 일치하는지 검증합니다.
     * @param customData 포트원 결제 정보
     * @param orderResponse 주문 서비스에서 가져온 주문 정보
     */
    public void checkPaymentCalculation(PortOneCustomData customData, InternalOrderResponse orderResponse) {

        // 주문 ID 검증
        if (!customData.orderId().equals(orderResponse.orderId())) {
            log.error("주문 ID가 일치하지 않습니다. 결제 정보: {}, 주문 정보: {}", customData.orderId(), orderResponse.orderId());
            throw new PaymentException(PAYMENT_ITEMS_MISMATCH, "주문 ID가 일치하지 않습니다.");
        }

        // 결제 금액 계산
        int totalPaymentAmount = calculateTotalPaymentAmount(customData.purchasedItems());
        long totalOrderAmount = calculateTotalOrderAmount(orderResponse.objects());

        // 결제 금액과 주문 금액 비교
        if (totalPaymentAmount != totalOrderAmount) {
            log.error("결제 금액과 주문 금액이 일치하지 않습니다. 결제 금액: {}, 주문 금액: {}", totalPaymentAmount, totalOrderAmount);
            throw new PaymentException(PAYMENT_CALCULATION_FAILED,
                                       String.format("결제 금액(%d)과 주문 금액(%d)이 일치하지 않습니다.", totalPaymentAmount, totalOrderAmount));
        }

        // 결제된 상품과 주문 상품 비교
        verifyPurchasedItems(customData.purchasedItems(), orderResponse.objects());

        log.info("결제 금액 계산 검증 성공. 결제 금액: {}, 주문 금액: {}", totalPaymentAmount, totalOrderAmount);

    }

    /**
     * 결제된 상품 목록의 총 금액을 계산합니다.
     * @param purchasedItems 결제된 상품 목록
     * @return 총 결제 금액
     */
    private int calculateTotalPaymentAmount(List<PurchasedItem> purchasedItems) {
        return purchasedItems.stream()
                .mapToInt(item -> item.price() * item.quantity())
                .sum();
    }

    /**
     * 주문된 상품 목록의 총 금액을 계산합니다.
     * @param orderedItems 주문된 상품 목록
     * @return 총 주문 금액
     */
    private long calculateTotalOrderAmount(List<PurchasedObjectResponse> orderedItems) {
        return orderedItems.stream()
                .mapToLong(item -> item.purchasePrice() * item.quantity())
                .sum();
    }

    /**
     * 결제된 상품과 주문 상품이 일치하는지 검증합니다.
     * 상품 ID, 수량, 가격을 비교합니다.
     * @param purchasedItems 결제된 상품 목록
     * @param orderedItems 주문된 상품 목록
     */
    private void verifyPurchasedItems(List<PurchasedItem> purchasedItems, List<PurchasedObjectResponse> orderedItems) {
        // 상품 개수 비교
        if (purchasedItems.size() != orderedItems.size()) {
            log.error("결제된 상품 개수와 주문 상품 개수가 일치하지 않습니다. 결제 상품 개수: {}, 주문 상품 개수: {}",
                      purchasedItems.size(), orderedItems.size());
            throw new PaymentException(PAYMENT_ITEMS_MISMATCH,
                                       "결제된 상품 개수와 주문 상품 개수가 일치하지 않습니다.");
        }

        Map<String, PurchasedItem> purchasedItemMap = purchasedItems.stream()
                .collect(Collectors.toMap(PurchasedItem::productId, Function.identity()));

        // 각 주문 상품에 대해 결제된 상품과 비교
        for (PurchasedObjectResponse orderedItem : orderedItems) {
            PurchasedItem purchasedItem = purchasedItemMap.get(orderedItem.objectId());

            // 상품 ID 검증
            if (purchasedItem == null) {
                log.error("주문한 상품이 결제 정보에 없습니다. 상품 ID: {}", orderedItem.objectId());
                throw new PaymentException(PAYMENT_ITEMS_MISMATCH,
                                           "주문한 상품이 결제 정보에 없습니다. 상품 ID: " + orderedItem.objectId());
            }

            // 상품 수량 검증
            if (purchasedItem.quantity() != orderedItem.quantity()) {
                log.error("상품 수량이 일치하지 않습니다. 상품 ID: {}, 결제 수량: {}, 주문 수량: {}",
                          orderedItem.objectId(), purchasedItem.quantity(), orderedItem.quantity());
                throw new PaymentException(PAYMENT_ITEMS_MISMATCH,
                                           String.format("상품 수량이 일치하지 않습니다. 상품 ID: %s, 결제 수량: %d, 주문 수량: %d",
                                                         orderedItem.objectId(), purchasedItem.quantity(), orderedItem.quantity()));
            }

            // 상품 가격 검증
            if (purchasedItem.price() != orderedItem.purchasePrice()) {
                log.error("상품 가격이 일치하지 않습니다. 상품 ID: {}, 결제 가격: {}, 주문 가격: {}",
                          orderedItem.objectId(), purchasedItem.price(), orderedItem.purchasePrice());
                throw new PaymentException(PAYMENT_ITEMS_MISMATCH,
                                           String.format("상품 가격이 일치하지 않습니다. 상품 ID: %s, 결제 가격: %d, 주문 가격: %d",
                                                         orderedItem.objectId(), purchasedItem.price(), orderedItem.purchasePrice()));
            }
        }
    }

}
