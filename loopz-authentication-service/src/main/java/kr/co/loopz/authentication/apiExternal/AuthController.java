package kr.co.loopz.authentication.apiExternal;

import jakarta.validation.Valid;
import kr.co.loopz.authentication.dto.request.TokenRequest;
import kr.co.loopz.authentication.dto.response.SocialLoginResponse;
import kr.co.loopz.authentication.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @Valid @RequestBody TokenRequest tokenRequest) {

        SocialLoginResponse socialLoginResponse = authService.loginOrRegister(tokenRequest);
        String accessWithBearer = authService.createAccessTokenWhenLogin(socialLoginResponse.userId());

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, accessWithBearer)
                .body(socialLoginResponse);

    }

}
