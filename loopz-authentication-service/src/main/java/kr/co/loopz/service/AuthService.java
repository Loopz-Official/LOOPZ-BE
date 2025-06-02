package kr.co.loopz.service;

import kr.co.loopz.client.GoogleClient;
import kr.co.loopz.client.UserServiceClient;
import kr.co.loopz.dto.request.InternalRegisterRequest;
import kr.co.loopz.dto.request.TokenRequest;
import kr.co.loopz.dto.response.GoogleResourceServerResponse;
import kr.co.loopz.dto.response.InternalRegisterResponse;
import kr.co.loopz.dto.response.SocialLoginResponse;
import kr.co.loopz.jwt.JwtProvider;
import kr.co.loopz.redis.service.RefreshTokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static kr.co.loopz.constants.SecurityConstants.TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final GoogleClient googleClient;
    private final UserServiceClient userServiceClient;

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

        String bearerHeader = TOKEN_PREFIX + tokenRequest.accessToken();

        // 구글 리소스 서버에 access token으로 사용자 정보 요청
        GoogleResourceServerResponse googleUserInfo = googleClient.getUserInfo(bearerHeader);

        // user-service에 회원가입/로그인 요청
        InternalRegisterRequest request = InternalRegisterRequest.from(googleUserInfo);
        InternalRegisterResponse user = userServiceClient.getOrCreateUser(request);

        return SocialLoginResponse.from(user);
    }


    public String createAccessTokenWhenLogin(String userId) {

        Authentication authentication = jwtProvider.getAuthenticationFromUserId(userId);
        String accessToken = jwtProvider.generateAccessToken(authentication, userId);
        String refreshToken = jwtProvider.generateRefreshToken(authentication, userId);

        refreshTokenRedisService.saveRefreshToken(userId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME);

        return TOKEN_PREFIX + accessToken;
    }

}
