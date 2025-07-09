package kr.co.loopz.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KakaoResourceServerResponse(
        Long id,
        @JsonProperty("kakao_account")
        @NotNull @Valid
        KakaoAccount kakaoAccount,
        @NotNull @Valid
        KakaoProperties properties
) {
    public record KakaoAccount(
            @JsonProperty("has_email")
            Boolean hasEmail,
            @JsonProperty("email_needs_agreement")
            Boolean emailNeedsAgreement,
            @JsonProperty("is_email_valid")
            Boolean isEmailValid,
            @JsonProperty("is_email_verified")
            Boolean isEmailVerified,
            @NotBlank @Email
            String email
    ) {}

    public record KakaoProperties(
            @NotBlank
            String nickname,
            @JsonProperty("profile_image") String profileImage,
            @JsonProperty("thumbnail_image") String thumbnailImage
    ) {}
}
