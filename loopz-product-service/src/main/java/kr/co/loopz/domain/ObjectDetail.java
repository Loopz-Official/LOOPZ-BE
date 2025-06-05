package kr.co.loopz.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ObjectDetail {

    @Column(nullable = false)
    private int shippingPrice;
    private String shippingInfo;

    private String size;

    // HTML로 받을 상세 설명 (버킷주소)
    private String descriptionUrl;

    // 상품 정보
    private String productInfo;

    // 구매 전 읽어주세요
    private String notice;
}
