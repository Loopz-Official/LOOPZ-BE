package kr.co.loopz.search.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.co.loopz.common.domain.BaseTimeEntity;
import kr.co.loopz.common.domain.BaseTimeEntityWithDeletion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@AllArgsConstructor
@SQLDelete(sql = "UPDATE search SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Search extends BaseTimeEntityWithDeletion {

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
