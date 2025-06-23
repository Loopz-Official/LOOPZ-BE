package kr.co.loopz.object.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import kr.co.loopz.object.Exception.ObjectException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.co.loopz.object.Exception.ObjectErrorCode.INSUFFICIENT_STOCK;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ObjectDetail {

    private String size;

    // HTML로 받을 상세 설명 (버킷주소)
    private String descriptionUrl;

    // 상품 정보
    private String productInfo;

    // 구매 전 읽어주세요
    private String notice;

    // 입고 수량
    private int stock;


    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new ObjectException(INSUFFICIENT_STOCK);
        }
        this.stock -= quantity;
    }

}
