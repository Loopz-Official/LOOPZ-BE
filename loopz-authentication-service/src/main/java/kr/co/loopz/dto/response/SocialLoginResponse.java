package kr.co.loopz.dto.response;

public record SocialLoginResponse(
        String userId,
        String email,
        boolean isEnabled
) {

    public static SocialLoginResponse from(InternalRegisterResponse response) {
        return new SocialLoginResponse(
                response.userId(),
                response.email(),
                response.isEnabled()
        );
    }

}
