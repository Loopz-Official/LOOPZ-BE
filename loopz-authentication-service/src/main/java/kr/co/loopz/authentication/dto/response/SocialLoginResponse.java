package kr.co.loopz.authentication.dto.response;

public record SocialLoginResponse(
        String userId,
        String email,
        boolean isEnabled,
        String realName
) {
}
