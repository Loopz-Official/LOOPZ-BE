package kr.co.loopz.user.dto.response;

import kr.co.loopz.user.domain.SocialLoginType;

public record UserInfoResponse(
        String userId,
        String email,
        String realName,
        String loginName,
        String nickName,
        String imageUrl,
        boolean enabled,
        SocialLoginType socialLoginType
) {
}
