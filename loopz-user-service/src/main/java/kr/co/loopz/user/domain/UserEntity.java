package kr.co.loopz.user.domain;

import jakarta.persistence.*;
import kr.co.loopz.common.domain.BaseTimeEntityWithDeletion;
import kr.co.loopz.user.dto.request.UserInternalRegisterRequest;
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
public class UserEntity extends BaseTimeEntityWithDeletion {

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

    @Column(nullable = true, unique = true)
    private String nickName;

    @Column(length = 1024)
    private String imageUrl;

    @ColumnDefault("false")
    private boolean isEnabled;

    @Enumerated(STRING)
    private Role role;


    public static UserEntity from(UserInternalRegisterRequest request) {
        return UserEntity.builder()
                .email(request.email())
                .realName(request.name())
                .imageUrl(request.picture())

                .role(Role.USER)
                .isEnabled(true)
                .build();
    }

    @Builder(access = PRIVATE)
    private UserEntity(String email, String realName, String nickName, boolean isEnabled, Role role, String imageUrl) {

        this.userId = UUID.randomUUID().toString();

        this.email = email;
        this.realName = realName;
        this.nickName = nickName;
        this.imageUrl = imageUrl;

        this.role = role;
        this.isEnabled = isEnabled;
    }
}
