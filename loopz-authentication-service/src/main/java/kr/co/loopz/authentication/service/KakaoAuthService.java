package kr.co.loopz.authentication.service;

import kr.co.loopz.authentication.client.KakaoResourceClient;
import kr.co.loopz.authentication.client.KakaoTokenClient;
import kr.co.loopz.authentication.client.UserServiceClient;
import kr.co.loopz.authentication.converter.AuthConverter;
import kr.co.loopz.authentication.dto.request.InternalRegisterRequest;
import kr.co.loopz.authentication.dto.response.InternalRegisterResponse;
import kr.co.loopz.authentication.dto.response.KakaoResourceServerResponse;
import kr.co.loopz.authentication.dto.response.KakaoTokenResponse;
import kr.co.loopz.authentication.dto.response.SocialLoginResponse;
import kr.co.loopz.authentication.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import static kr.co.loopz.authentication.constants.SecurityConstants.TOKEN_PREFIX;
import static kr.co.loopz.authentication.exception.AuthenticationErrorCode.KAKAO_AUTHENTICATION_FAILED;
import static kr.co.loopz.authentication.exception.AuthenticationErrorCode.USER_SERVICE_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {

    private final KakaoTokenClient kakaoTokenClient;
    private final KakaoResourceClient kakaoResourceClient;
    private final UserServiceClient userServiceClient;
    private final AuthConverter authConverter;

    @Value("${etc.kakao.client-id}")
    private String KAKAO_CLIENT_ID;
    @Value("${etc.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    public SocialLoginResponse loginOrRegisterKakao(String accessCode) {
        KakaoResourceServerResponse kakaoUserInfo = requestToKakao(accessCode);
        log.debug("카카오 사용자 정보: {}", kakaoUserInfo);

        InternalRegisterResponse user = requestToUserService(authConverter.toInternalRegisterRequest(kakaoUserInfo));

        return authConverter.toSocialLoginResponse(user);
    }

    private KakaoResourceServerResponse requestToKakao(String accessCode) {
        try {
            MultiValueMap<String, Object> formData = authConverter.toKakaoTokenRequest(accessCode, KAKAO_CLIENT_ID, KAKAO_REDIRECT_URI);
            KakaoTokenResponse kakaoToken = kakaoTokenClient.getKakaoToken(formData);
            log.debug("카카오 토큰 정보: {}", kakaoToken);

            String bearerHeader = TOKEN_PREFIX + kakaoToken.accessToken();
            return kakaoResourceClient.getUserInfo(bearerHeader);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 실패: {}", e.getMessage());
            throw new AuthenticationException(KAKAO_AUTHENTICATION_FAILED, e.getMessage() + "사용자 이메일 값은 필수입니다.");
        }
    }

    private InternalRegisterResponse requestToUserService(InternalRegisterRequest internalRegisterRequest) {
        try {
            InternalRegisterResponse user = userServiceClient.getOrCreateUser(internalRegisterRequest);
            log.debug("user-service에서 반환된 사용자 정보: {}", user);
            return user;
        } catch (Exception e) {
            log.error("user-service 요청 중 오류 발생: {}", e.getMessage());
            throw new AuthenticationException(USER_SERVICE_FAILED);
        }
    }
}