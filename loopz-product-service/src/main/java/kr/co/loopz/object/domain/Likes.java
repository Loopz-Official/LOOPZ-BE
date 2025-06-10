package kr.co.loopz.object.domain;

import jakarta.persistence.*;
import kr.co.loopz.common.domain.BaseTimeEntity;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@AllArgsConstructor
@Table(name = "`like`")
public class Likes extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String objectId;


    @Builder(access = AccessLevel.PRIVATE)
    public Likes(String userId, String objectId) {
        this.userId = userId;
        this.objectId = objectId;
    }

    public static Likes from(String userId, String objectId) {
        return Likes.builder()
                .userId(userId)
                .objectId(objectId)
                .build();
    }
}
