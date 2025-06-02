package kr.co.loopz.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "`user`")
@NoArgsConstructor(access = PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE `user` SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class UserEntity extends BaseTimeEntityWithDeletion{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    // soft delete 후 재 가입을 위해 unique false
    @Column(length = 30, nullable = false, unique = false)
    private String email;

    // 실명
    @Column(length = 30)
    private String realName;

    @Column(nullable = false, unique = true)
    private String nickName;

    @ColumnDefault("false")
    private boolean isEnabled;

    @Enumerated(STRING)
    private Role role;



    @Builder(access = PRIVATE)
    private UserEntity(String email, String realName, String nickName, boolean isEnabled, Role role) {

        this.userId = UUID.randomUUID().toString();

        this.email = email;
        this.realName = realName;
        this.nickName = nickName;

        this.role = role;
        this.isEnabled = isEnabled;
    }
}
