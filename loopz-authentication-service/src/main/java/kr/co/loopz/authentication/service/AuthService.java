package kr.co.loopz.authentication.service;

import kr.co.loopz.authentication.client.GoogleClient;
import kr.co.loopz.authentication.client.UserServiceClient;
import kr.co.loopz.authentication.constants.SecurityConstants;
import kr.co.loopz.authentication.converter.AuthConverter;
import kr.co.loopz.authentication.dto.request.InternalRegisterRequest;
import kr.co.loopz.authentication.dto.request.TokenRequest;
import kr.co.loopz.authentication.dto.response.GoogleResourceServerResponse;
import kr.co.loopz.authentication.dto.response.InternalRegisterResponse;
import kr.co.loopz.authentication.dto.response.LogoutResponse;
import kr.co.loopz.authentication.dto.response.SocialLoginResponse;
import kr.co.loopz.authentication.exception.AuthenticationException;
import kr.co.loopz.authentication.jwt.JwtProvider;
import kr.co.loopz.common.redis.service.RefreshTokenRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static kr.co.loopz.authentication.exception.AuthenticationErrorCode.GOOGLE_AUTHENTICATION_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtProvider jwtProvider;
    private final GoogleClient googleClient;
    private final UserServiceClient userServiceClient;

    private final AuthConverter authConverter;

    private final RefreshTokenRedisService refreshTokenRedisService;
    @Value("${jwt.expiration.refresh}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;

    /**
     * 구글 로그인 또는 회원가입
     * 받은 access token으로 구글 리소스 서버에 요청
     * user-service로 회원가입/로그인 요청
     * @param tokenRequest
     * @return SocialLoginResponse
     */
    public SocialLoginResponse loginOrRegister(TokenRequest tokenRequest) {

        String bearerHeader = SecurityConstants.TOKEN_PREFIX + tokenRequest.accessToken();

        // 구글 리소스 서버에 access token으로 사용자 정보 요청
        GoogleResourceServerResponse googleUserInfo = requestToGoogle(bearerHeader);
        log.debug("구글 사용자 정보: {}", googleUserInfo);

        // user-service에 회원가입/로그인 요청
        InternalRegisterRequest request = authConverter.toInternalRegisterRequest(googleUserInfo);
        InternalRegisterResponse user = userServiceClient.getOrCreateUser(request);
        log.debug("user-service에서 반환된 사용자 정보: {}", user);

        return authConverter.toSocialLoginResponse(user);
    }


    public String createAccessTokenWhenLogin(String userId) {

        Authentication authentication = jwtProvider.getAuthenticationFromUserId(userId);
        String accessToken = jwtProvider.generateAccessToken(authentication, userId);
        String refreshToken = jwtProvider.generateRefreshToken(authentication, userId);

        refreshTokenRedisService.saveRefreshToken(userId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME);

        return SecurityConstants.TOKEN_PREFIX + accessToken;
    }

    public LogoutResponse logout(String userId) {
        boolean isDeleted = refreshTokenRedisService.deleteRefreshToken(userId);
        String message = isDeleted ? "Logout successful" : "Logout failed: User not found";
        return new LogoutResponse(message);
    }



    private GoogleResourceServerResponse requestToGoogle(String bearerHeader) {

        GoogleResourceServerResponse googleUserInfo;
        try {
            googleUserInfo = googleClient.getUserInfo(bearerHeader);
        } catch (Exception e) {
            log.info("구글 리소스 서버 요청 실패: {}", e.getMessage());
            throw new AuthenticationException(GOOGLE_AUTHENTICATION_FAILED, e.getMessage());
        }
        return googleUserInfo;

    }


}
