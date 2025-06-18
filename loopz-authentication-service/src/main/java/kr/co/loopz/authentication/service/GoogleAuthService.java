package kr.co.loopz.authentication.service;

import kr.co.loopz.authentication.client.GoogleResourceClient;
import kr.co.loopz.authentication.client.UserServiceClient;
import kr.co.loopz.authentication.converter.AuthConverter;
import kr.co.loopz.authentication.dto.request.InternalRegisterRequest;
import kr.co.loopz.authentication.dto.request.TokenRequest;
import kr.co.loopz.authentication.dto.response.GoogleResourceServerResponse;
import kr.co.loopz.authentication.dto.response.InternalRegisterResponse;
import kr.co.loopz.authentication.dto.response.SocialLoginResponse;
import kr.co.loopz.authentication.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static kr.co.loopz.authentication.constants.SecurityConstants.TOKEN_PREFIX;
import static kr.co.loopz.authentication.exception.AuthenticationErrorCode.*;
import static kr.co.loopz.authentication.exception.AuthenticationErrorCode.GOOGLE_AUTHENTICATION_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthService {

    private final GoogleResourceClient googleResourceClient;
    private final UserServiceClient userServiceClient;
    private final AuthConverter authConverter;

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