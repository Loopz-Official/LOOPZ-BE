package kr.co.loopz.service;

import kr.co.loopz.authentication.client.GoogleClient;
import kr.co.loopz.authentication.client.UserServiceClient;
import kr.co.loopz.authentication.dto.request.InternalRegisterRequest;
import kr.co.loopz.authentication.dto.request.TokenRequest;
import kr.co.loopz.authentication.dto.response.GoogleResourceServerResponse;
import kr.co.loopz.authentication.dto.response.InternalRegisterResponse;
import kr.co.loopz.authentication.dto.response.SocialLoginResponse;
import kr.co.loopz.authentication.jwt.JwtProvider;
import kr.co.loopz.common.redis.service.RefreshTokenRedisService;
import kr.co.loopz.authentication.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static kr.co.loopz.authentication.constants.SecurityConstants.TOKEN_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * AuthService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private GoogleClient googleClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private RefreshTokenRedisService refreshTokenRedisService;

    @InjectMocks
    private AuthService authService;

    // @Value("${jwt.expiration.refresh}")
    @Captor
    private ArgumentCaptor<String> userIdCaptor;

    @Captor
    private ArgumentCaptor<String> refreshTokenCaptor;

    @Captor
    private ArgumentCaptor<Long> expireTimeCaptor;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "REFRESH_TOKEN_EXPIRE_TIME", 3600L);
    }

    @Test
    void loginOrRegister_정상흐름() {

        // given
        TokenRequest tokenRequest = new TokenRequest("dummyGoogleAccessToken", "google");

        GoogleResourceServerResponse googleResponse = new GoogleResourceServerResponse(
                "1234",
                "hong@example.com",
                true,
                "홍길동",
                "길동",
                "홍",
                "https://example.com/profile.jpg",
                "KR"
        );


        // when
        when(googleClient.getUserInfo(eq(TOKEN_PREFIX + tokenRequest.accessToken())))
                .thenReturn(googleResponse);

        // 3) userServiceClient.getOrCreateUser(...) 목 설정
        InternalRegisterResponse registerResponse = new InternalRegisterResponse(
                "hong@example.com",
                "12341234",
                "홍길동",
                true,
                "길동"
        );

        // getOrCreateUser의 실제 인자로는 InternalRegisterRequest.from(googleResponse)가 전달됨
        when(userServiceClient.getOrCreateUser(any(InternalRegisterRequest.class)))
                .thenReturn(registerResponse);

        // 4) 메서드 실행
        SocialLoginResponse result = authService.loginOrRegister(tokenRequest);

        // 5) 검증: 리턴값
        assertNotNull(result);
        assertEquals("12341234", result.userId());
        assertEquals("홍길동", result.realName());
        assertEquals("hong@example.com", result.email());

        // 6) 각 의존 객체가 정확히 호출됐는지 verify
        verify(googleClient, times(1)).getUserInfo(TOKEN_PREFIX + tokenRequest.accessToken());
        verify(userServiceClient, times(1)).getOrCreateUser(any(InternalRegisterRequest.class));
    }

    @Test
    void createAccessTokenWhenLogin_정상흐름() {
        // 1) 준비: userId
        String userId = "user-123";

        // 2) jwtProvider.getAuthenticationFromUserId(...) 목 설정
        Authentication mockAuth = mock(Authentication.class);
        when(jwtProvider.getAuthenticationFromUserId(userId))
                .thenReturn(mockAuth);

        // 3) jwtProvider.generateAccessToken(...) 목 설정
        String dummyAccessToken = "access.jwt.token";
        when(jwtProvider.generateAccessToken(eq(mockAuth), eq(userId)))
                .thenReturn(dummyAccessToken);

        // 4) jwtProvider.generateRefreshToken(...) 목 설정
        String dummyRefreshToken = "refresh.jwt.token";
        when(jwtProvider.generateRefreshToken(eq(mockAuth), eq(userId)))
                .thenReturn(dummyRefreshToken);

        // 5) 실제 메서드 호출
        String returned = authService.createAccessTokenWhenLogin(userId);

        // 6) 반환값 검증: TOKEN_PREFIX가 붙어야 함
        assertEquals(TOKEN_PREFIX + dummyAccessToken, returned);

        // 7) refreshTokenRedisService.saveRefreshToken(...) 호출 검증
        // userId, dummyRefreshToken, REFRESH_TOKEN_EXPIRE_TIME(=3600L)을 인자로 받았는지 확인
        verify(refreshTokenRedisService, times(1))
                .saveRefreshToken(userIdCaptor.capture(), refreshTokenCaptor.capture(), expireTimeCaptor.capture());

        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(dummyRefreshToken, refreshTokenCaptor.getValue());
        assertEquals(3600L, expireTimeCaptor.getValue());
    }

    @Test
    void createAccessTokenWhenLogin_jwtProvider에서예외발생시_메서드전파() {
        // jwtProvider.getAuthenticationFromUserId()에서 예외가 발생하면 그대로 전파되는지 검증
        String userId = "user-456";
        when(jwtProvider.getAuthenticationFromUserId(userId))
                .thenThrow(new RuntimeException("인증 생성 실패"));

        // 예외가 발생할 때, 해당 예외가 그대로 던져지는지 확인
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            authService.createAccessTokenWhenLogin(userId);
        });
        assertEquals("인증 생성 실패", ex.getMessage());

        // refreshTokenRedisService는 호출되지 않아야 함
        verifyNoInteractions(refreshTokenRedisService);
    }
}
