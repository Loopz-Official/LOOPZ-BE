package kr.co.loopz.authentication.dto.response;

public record SocialLoginResponse(
        String userId,
        String email,
        String loginName,
        String realName,
        String nickName,
        boolean enabled
) {
}
