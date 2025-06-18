package kr.co.loopz.authentication.apiExternal;

import jakarta.validation.Valid;
import kr.co.loopz.authentication.dto.request.TokenRequest;
import kr.co.loopz.authentication.dto.response.LogoutResponse;
import kr.co.loopz.authentication.dto.response.SocialLoginResponse;
import kr.co.loopz.authentication.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthController {

    private final AuthService authService;

    /**
     * 구글 로그인
     * 받은 access token으로 구글 리소스 서버에 요청
     * user-service로 질의
     * db에 존재하면 반환, 없으면 추가
     * @param tokenRequest
     * @return SocialLoginResponse
     */
    @PostMapping("/login/google")
    public ResponseEntity<SocialLoginResponse> googleLogin (
            @Valid @RequestBody TokenRequest tokenRequest
    ) {

        SocialLoginResponse socialLoginResponse = authService.loginOrRegisterGoogle(tokenRequest);
        String accessWithBearer = authService.createAccessTokenWhenLogin(socialLoginResponse.userId());

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, accessWithBearer)
                .body(socialLoginResponse);

    }


    @GetMapping("/login/kakao")
    public ResponseEntity<SocialLoginResponse> kakaoLogin(
            @RequestParam("code") String accessCode
    ) {

        SocialLoginResponse socialLoginResponse = authService.loginOrRegisterKakao(accessCode);
        String accessWithBearer = authService.createAccessTokenWhenLogin(socialLoginResponse.userId());

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, accessWithBearer)
                .body(socialLoginResponse);

    }


    @PostMapping("/login/naver")
    public ResponseEntity<SocialLoginResponse> naverLogin(
            @Valid @RequestBody TokenRequest tokenRequest
    ) {

        SocialLoginResponse socialLoginResponse = authService.loginOrRegisterNaver(tokenRequest);
        String accessWithBearer = authService.createAccessTokenWhenLogin(socialLoginResponse.userId());

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, accessWithBearer)
                .body(socialLoginResponse);

    }


    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @AuthenticationPrincipal User currentUser
            ) {

        String userId = currentUser.getUsername();

        LogoutResponse response = authService.logout(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
