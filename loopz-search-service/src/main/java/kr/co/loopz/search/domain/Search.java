package kr.co.loopz.search.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.co.loopz.common.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@AllArgsConstructor
public class Search extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String searchId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String content;


    @Builder
    public Search(String userId,
                  String content) {
        this.searchId = UUID.randomUUID().toString();
        this.userId = userId;
        this.content= content;

    }
}
