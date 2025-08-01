package kr.co.loopz.object.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.co.loopz.common.domain.BaseTimeEntity;
import kr.co.loopz.common.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@AllArgsConstructor
public class ObjectImage extends Image {

    // Object 참조
    @Column(name = "object_id", nullable = false)
    private String objectId;

    @Builder
    public ObjectImage(String imageUrl, String objectId) {
        this.imageUrl = imageUrl;
        this.objectId = objectId;
    }

    public ObjectImage replaceImage(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
}
