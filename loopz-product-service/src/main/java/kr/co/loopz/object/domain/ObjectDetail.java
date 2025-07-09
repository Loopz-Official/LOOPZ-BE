package kr.co.loopz.object.domain;

import jakarta.persistence.Embeddable;
import kr.co.loopz.object.exception.ObjectException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.co.loopz.object.exception.ObjectErrorCode.INSUFFICIENT_STOCK;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ObjectDetail {

    private String size;

    // HTML로 받을 상세 설명 (버킷주소)
    private String descriptionUrl;

    // 입고 수량
    private int stock;


    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new ObjectException(INSUFFICIENT_STOCK,"현재 재고: " + this.stock);
        }
        this.stock -= quantity;
    }

}
