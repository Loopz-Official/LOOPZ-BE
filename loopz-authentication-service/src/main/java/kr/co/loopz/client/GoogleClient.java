package kr.co.loopz.client;

import kr.co.loopz.dto.response.GoogleResourceServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(
        name = "google-oauth-client",
        url  = "${etc.google-profile-url}"
)
public interface GoogleClient {

    /**
     * 구글의 /userinfo 엔드포인트를 호출하여 사용자 정보를 가져옴
     * @param authorizationHeader "Bearer {accessToken}" 형태로 전달
     * @return ResponseGoogleAccess 구글이 리턴하는 사용자 정보
     */
    @GetMapping
    GoogleResourceServerResponse getUserInfo(
            @RequestHeader(AUTHORIZATION) String authorizationHeader
    );

}
