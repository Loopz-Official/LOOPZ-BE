package kr.co.loopz.object.domain;

import jakarta.persistence.*;

import kr.co.loopz.common.domain.BaseTimeEntity;
import kr.co.loopz.object.domain.enums.ObjectSize;
import kr.co.loopz.object.domain.enums.ObjectType;
import kr.co.loopz.object.domain.enums.Keyword;

import lombok.*;

import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@AllArgsConstructor
@Table(name = "`object`")
public class ObjectEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String objectId;

    @Column(unique = true, nullable = false)
    private String objectName;

    @Column(nullable = false)
    private Long objectPrice;

    @Column(nullable = false)
    private boolean soldOut = false;

    // 한줄 소개
    @Column(nullable = false)
    private String intro;

    @Enumerated(EnumType.STRING)
    private ObjectType objectType;

    @Enumerated(EnumType.STRING)
    private ObjectSize objectSize;

    // 키워드 (다중선택 가능)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "object_keywords", joinColumns = @JoinColumn(name = "object_id", referencedColumnName = "objectId"))
    @Column(name = "keyword")
    @Enumerated(EnumType.STRING)
    private Set<Keyword> keywords = new HashSet<>();

    @Embedded
    private ObjectDetail detail;


    @Builder(access = AccessLevel.PRIVATE)
    private ObjectEntity(String objectName, Long objectPrice, String intro, ObjectType objectType, ObjectSize objectSize, Set<Keyword> keywords, ObjectDetail detail, int likeCount) {
        this.objectId = UUID.randomUUID().toString();
        this.objectName = objectName;
        this.objectPrice = objectPrice;
        this.intro = intro;
        this.objectType = objectType;
        this.objectSize = objectSize;
        this.keywords = keywords;
        this.detail = detail;
    }

}