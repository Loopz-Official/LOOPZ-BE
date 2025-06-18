package kr.co.loopz.user.dto.request;


import jakarta.validation.constraints.NotBlank;
import kr.co.loopz.user.domain.SocialLoginType;

public record UserInternalRegisterRequest(
        @NotBlank
        String email,
        @NotBlank
        String name,
        String givenName,
        String familyName,
        String picture,
        @NotBlank
        SocialLoginType socialLoginType
) {
}
