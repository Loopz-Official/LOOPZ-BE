package kr.co.loopz.payment.domain.enums;

/**
 * PENDING, ORDERED는 결제와만 관련 있는 상태
 * SHIPPING_PREPARE, SHIPPING, DELIVERED, PURCHASE_CONFIRMED는 주문과 관련 있는 상태, 4가지가 정상 flow
 * CANCEL_REQUESTED, CANCEL_COMPLETE, REFUND_REQUESTED, REFUND_COMPLETE는 결제 취소와 반품과 관련 있는 상태
 * 정상 flow가 아닌 분기 로직은 다음 2가지 경우만 가능
 * SHIPPING_PREPARE -> CANCEL REQUESTED
 * DELIVERED -> REFUND REQUESTED
 */
public enum OrderStatus {
    PENDING, // 주문 생성
    ORDERED, // 결제 성공 후 (결제 완료)

    SHIPPING_PREPARE, // 배송 준비
    SHIPPING, // 배송중
    DELIVERED, // 배송 완료
    PURCHASE_CONFIRMED, // 구매 확정

    CANCEL_REQUESTED, // 결제 취소 요청
    CANCEL_COMPLETE, // 결제 취소 완료
    REFUND_REQUESTED, // 반품 요청
    REFUND_COMPLETE, // 반품 완료
}
