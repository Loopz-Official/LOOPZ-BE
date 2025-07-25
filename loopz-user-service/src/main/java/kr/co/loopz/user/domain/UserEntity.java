package kr.co.loopz.user.domain;

import jakarta.persistence.*;
import kr.co.loopz.common.domain.BaseTimeEntityWithDeletion;
import kr.co.loopz.user.domain.enums.Gender;
import kr.co.loopz.user.domain.enums.Role;
import kr.co.loopz.user.domain.enums.SocialLoginType;
import kr.co.loopz.user.dto.request.UserInternalRegisterRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "`user`")
@NoArgsConstructor(access = PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE \"user\" SET deleted_at = NOW() WHERE id = ?")
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

    @Column(length =30)
    private String realName;

    // 이메일 이름
    @Column(length = 30)
    private String loginName;

    // 닉네임
    @Column(length = 20, nullable = true)
    private String nickName;

    @Column(length = 1024)
    private String imageUrl;

    private boolean enabled;

    @Enumerated(STRING)
    private Role role;

    @Enumerated(STRING)
    private SocialLoginType socialLoginType = SocialLoginType.NONE;

    @Embedded
    private UserTerms userTerms = new UserTerms();

    @Enumerated(STRING)
    private Gender gender;

    private LocalDate birthDate;

    /**
     * UserEntity 생성 메서드
     * 회원 가입시 사용
     */
    public static UserEntity from(UserInternalRegisterRequest registerRequest) {
        return UserEntity.builder()
                .email(registerRequest.email())
                .loginName(registerRequest.name())
                .imageUrl(registerRequest.picture())
                .socialLoginType(registerRequest.socialLoginType())
                .role(Role.USER)
                .enabled(false)
                .build();
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * 온보딩 과정에서 사용
     * 14세 이상, 약관 동의시 enabled 상태로 변경
     * @param over14
     * @param agreedServiceTerms
     * @param agreedMarketing
     * @param agreedEventSMS
     */
    public void updateTerms(boolean over14, boolean agreedServiceTerms, boolean agreedMarketing, boolean agreedEventSMS) {
        this.userTerms.updateTerms(over14, agreedServiceTerms, agreedMarketing, agreedEventSMS);

        if (over14 && agreedServiceTerms && this.nickName != null) {
            this.enabled = true;
        }
    }

    @Builder(access = PRIVATE)
    private UserEntity(String email, String loginName, String nickName, boolean enabled, Role role, String imageUrl, SocialLoginType socialLoginType) {

        this.userId = UUID.randomUUID().toString();

        this.email = email;
        this.loginName = loginName;
        this.nickName = nickName;
        this.imageUrl = imageUrl;

        this.role = role;
        this.enabled = enabled;
        this.socialLoginType = socialLoginType != null ? socialLoginType : SocialLoginType.NONE;
    }
}
