package kr.co.loopz.search.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Table(name = "object_search")
public class SearchObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String searchId;

    @Column(nullable = false)
    private String objectId;


    @Builder(access = AccessLevel.PRIVATE)
    public SearchObject(String searchId, String objectId) {
        this.searchId = searchId;
        this.objectId = objectId;
    }
}
