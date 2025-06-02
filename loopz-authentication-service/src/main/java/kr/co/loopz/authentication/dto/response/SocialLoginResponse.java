package kr.co.loopz.authentication.dto.response;

public record SocialLoginResponse(
        String userId,
        String email,
        boolean isEnabled,
        String realName
) {

    public static SocialLoginResponse from(InternalRegisterResponse response) {
        return new SocialLoginResponse(
                response.userId(),
                response.email(),
                response.isEnabled(),
                response.realName()
        );
    }

}
