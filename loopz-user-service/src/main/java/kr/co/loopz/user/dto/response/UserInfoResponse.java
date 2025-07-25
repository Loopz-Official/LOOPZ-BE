package kr.co.loopz.user.dto.response;

import kr.co.loopz.user.domain.enums.Gender;
import kr.co.loopz.user.domain.enums.SocialLoginType;

import java.time.LocalDate;

public record UserInfoResponse(
        String userId,
        String email,
        String realName,
        String loginName,
        String nickName,
        String imageUrl,
        boolean enabled,
        SocialLoginType socialLoginType,
        Gender gender,
        LocalDate birthDate
) {
}
