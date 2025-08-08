package kr.co.loopz.object.domain;

import jakarta.persistence.Embeddable;
import kr.co.loopz.object.domain.enums.Keyword;
import kr.co.loopz.object.domain.enums.ObjectSize;
import kr.co.loopz.object.domain.enums.ObjectType;
import kr.co.loopz.object.exception.ObjectException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

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

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ObjectDetail(String size, String descriptionUrl, int stock) {
        this.size = size;
        this.descriptionUrl = descriptionUrl;
        this.stock = stock;
    }

    public static ObjectDetail upload(String size, String descriptionUrl, int stock) {
        return ObjectDetail.builder()
                .size(size)
                .descriptionUrl(descriptionUrl)
                .stock(stock)
                .build();
    }
}
