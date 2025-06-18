package kr.co.loopz.authentication.service;

import kr.co.loopz.authentication.client.GoogleResourceClient;
import kr.co.loopz.authentication.client.KakaoResourceClient;
import kr.co.loopz.authentication.client.KakaoTokenClient;
import kr.co.loopz.authentication.client.UserServiceClient;
import kr.co.loopz.authentication.converter.AuthConverter;
import kr.co.loopz.authentication.dto.request.InternalRegisterRequest;
import kr.co.loopz.authentication.dto.request.TokenRequest;
import kr.co.loopz.authentication.dto.response.*;
import kr.co.loopz.authentication.exception.AuthenticationException;
import kr.co.loopz.authentication.jwt.JwtProvider;
import kr.co.loopz.common.redis.service.RefreshTokenRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import static kr.co.loopz.authentication.constants.SecurityConstants.TOKEN_PREFIX;
import static kr.co.loopz.authentication.exception.AuthenticationErrorCode.GOOGLE_AUTHENTICATION_FAILED;
import static kr.co.loopz.authentication.exception.AuthenticationErrorCode.USER_SERVICE_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtProvider jwtProvider;

    private final GoogleResourceClient googleResourceClient;
    private final KakaoTokenClient kakaoTokenClient;
    private final KakaoResourceClient kakaoResourceClient;
    private final UserServiceClient userServiceClient;

    private final AuthConverter authConverter;

    private final RefreshTokenRedisService refreshTokenRedisService;
    @Value("${jwt.expiration.refresh}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;

    @Value("${etc.kakao.client-id}")
    private String KAKAO_CLIENT_ID;
    @Value("${etc.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    /**
     * 구글 로그인 또는 회원가입
     * 받은 access token으로 구글 리소스 서버에 요청
     * user-service로 회원가입/로그인 요청
     * @param tokenRequest
     * @return SocialLoginResponse
     */
    public SocialLoginResponse loginOrRegisterGoogle(TokenRequest tokenRequest) {

        String bearerHeader = TOKEN_PREFIX + tokenRequest.accessToken();

        // 구글 리소스 서버에 access token으로 사용자 정보 요청
        GoogleResourceServerResponse googleUserInfo = requestToGoogle(bearerHeader);
        log.debug("구글 사용자 정보: {}", googleUserInfo);

        InternalRegisterResponse user = requestToUserService(authConverter.toInternalRegisterRequest(googleUserInfo));

        return authConverter.toSocialLoginResponse(user);
    }


    public SocialLoginResponse loginOrRegisterKakao(String accessCode) {

        KakaoResourceServerResponse kakaoUserInfo = requestToKakao(accessCode);
        log.debug("카카오 사용자 정보: {}", kakaoUserInfo);

        InternalRegisterResponse user = requestToUserService(authConverter.toInternalRegisterRequest(kakaoUserInfo));

        return authConverter.toSocialLoginResponse(user);
    }


    public SocialLoginResponse loginOrRegisterNaver(TokenRequest tokenRequest) {

        return null;
    }

    private InternalRegisterResponse requestToUserService(InternalRegisterRequest internalRegisterRequest) {
        // user-service에 회원가입/로그인 요청
        try {
            InternalRegisterResponse user = userServiceClient.getOrCreateUser(internalRegisterRequest);
            log.debug("user-service에서 반환된 사용자 정보: {}", user);
            return user;

        } catch (Exception e) {
            log.error("user-service 요청 중 오류 발생: {}", e.getMessage());
            throw new AuthenticationException(USER_SERVICE_FAILED);
        }

    }

    public String createAccessTokenWhenLogin(String userId) {

        Authentication authentication = jwtProvider.getAuthenticationFromUserId(userId);
        String accessToken = jwtProvider.generateAccessToken(authentication, userId);
        String refreshToken = jwtProvider.generateRefreshToken(authentication, userId);

        refreshTokenRedisService.saveRefreshToken(userId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME);

        return TOKEN_PREFIX + accessToken;
    }


    public LogoutResponse logout(String userId) {
        boolean isDeleted = refreshTokenRedisService.deleteRefreshToken(userId);
        String message = isDeleted ? "Logout successful" : "Logout failed: User not found";
        return new LogoutResponse(message);
    }



    private GoogleResourceServerResponse requestToGoogle(String bearerHeader) {

        GoogleResourceServerResponse googleUserInfo;
        try {
            googleUserInfo = googleResourceClient.getUserInfo(bearerHeader);
        } catch (Exception e) {
            log.info("구글 리소스 서버 요청 실패: {}", e.getMessage());
            throw new AuthenticationException(GOOGLE_AUTHENTICATION_FAILED, e.getMessage());
        }
        return googleUserInfo;

    }

    private KakaoResourceServerResponse requestToKakao(String accessCode) {
        MultiValueMap<String, Object> formData = authConverter.toKakaoTokenRequest(accessCode, KAKAO_CLIENT_ID, KAKAO_REDIRECT_URI);
        KakaoTokenResponse kakaoToken = kakaoTokenClient.getKakaoToken(formData);
        log.debug("카카오 토큰 정보: {}", kakaoToken);

        String bearerHeader = TOKEN_PREFIX + kakaoToken.accessToken();
        return kakaoResourceClient.getUserInfo(bearerHeader);
    }

}
