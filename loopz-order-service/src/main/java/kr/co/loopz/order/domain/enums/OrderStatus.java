package kr.co.loopz.order.domain.enums;

public enum OrderStatus {
    PENDING, // 주문 생성
    ORDERED, // 결제 성공 후
    SHIPPING, // 배송중
    DELIVERED, // 배송 완료
    CANCELED, // 결제 취소
    REFUND_REQUESTED, // 반품 요청
    REFUND_COMPLETE, // 반품 완료
}
