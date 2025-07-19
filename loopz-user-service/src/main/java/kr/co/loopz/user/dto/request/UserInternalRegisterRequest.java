package kr.co.loopz.user.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.loopz.user.domain.enums.SocialLoginType;

public record UserInternalRegisterRequest(
        @NotBlank
        String email,
        @NotBlank
        String name,
        String givenName,
        String familyName,
        String picture,
        @NotNull
        SocialLoginType socialLoginType
) {
}
