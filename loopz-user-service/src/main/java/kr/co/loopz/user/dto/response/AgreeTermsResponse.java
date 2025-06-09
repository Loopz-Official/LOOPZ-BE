package kr.co.loopz.user.dto.response;

public record AgreeTermsResponse(
        String userId,
        String email,
        String loginName,
        String realName,
        String nickName,
        boolean enabled,
        Boolean over14,
        Boolean agreedTerms,
        Boolean agreedPrivacyPolicy,
        Boolean agreedSMSMarketing
) {
}