package kr.co.loopz.authentication.service;

import kr.co.loopz.authentication.dto.request.TokenRequest;
import kr.co.loopz.authentication.dto.response.LogoutResponse;
import kr.co.loopz.authentication.dto.response.SocialLoginResponse;
import kr.co.loopz.authentication.jwt.JwtProvider;
import kr.co.loopz.common.redis.service.RefreshTokenRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static kr.co.loopz.authentication.constants.SecurityConstants.TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final GoogleAuthService googleAuthService;
    private final KakaoAuthService kakaoAuthService;

    @Value("${jwt.expiration.refresh}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;

    /**
     * 구글 로그인 또는 회원가입
     * 받은 access token으로 구글 리소스 서버에 요청
     * user-service로 회원가입/로그인 요청
     * @param tokenRequest
     * @return SocialLoginResponse
     */
    public SocialLoginResponse loginOrRegisterGoogle(TokenRequest tokenRequest) {
        return googleAuthService.loginOrRegisterGoogle(tokenRequest);
    }

    /**
     * 카카오 로그인 또는 회원가입
     * @param accessCode
     * @return SocialLoginResponse
     */
    public SocialLoginResponse loginOrRegisterKakao(String accessCode) {
        return kakaoAuthService.loginOrRegisterKakao(accessCode);
    }

    /**
     * 네이버 로그인 또는 회원가입
     * @param tokenRequest
     * @return SocialLoginResponse
     */
    public SocialLoginResponse loginOrRegisterNaver(TokenRequest tokenRequest) {
        // Not implemented yet
        return null;
    }

    /**
     * 로그인 시 액세스 토큰 생성
     * @param userId
     * @return String
     */
    public String createAccessTokenWhenLogin(String userId) {
        Authentication authentication = jwtProvider.getAuthenticationFromUserId(userId);
        String accessToken = jwtProvider.generateAccessToken(authentication, userId);
        String refreshToken = jwtProvider.generateRefreshToken(authentication, userId);

        refreshTokenRedisService.saveRefreshToken(userId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME);

        return TOKEN_PREFIX + accessToken;
    }

    /**
     * 로그아웃
     * @param userId
     * @return LogoutResponse
     */
    public LogoutResponse logout(String userId) {
        boolean isDeleted = refreshTokenRedisService.deleteRefreshToken(userId);
        String message = isDeleted ? "Logout successful" : "Logout failed: User not found";
        return new LogoutResponse(message);
    }
}