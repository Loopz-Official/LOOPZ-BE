package kr.co.loopz.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoResourceServerResponse(
        Long id,
        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount,
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
            String email
    ) {}

    public record KakaoProperties(
            String nickname,
            @JsonProperty("profile_image") String profileImage,
            @JsonProperty("thumbnail_image") String thumbnailImage
    ) {}
}
