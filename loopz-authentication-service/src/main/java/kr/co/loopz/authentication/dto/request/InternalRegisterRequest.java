package kr.co.loopz.authentication.dto.request;

import kr.co.loopz.authentication.constants.SocialLoginType;

public record InternalRegisterRequest(
        String email,
        String name,
        String givenName,
        String familyName,
        String picture,
        SocialLoginType socialLoginType
) {
}
