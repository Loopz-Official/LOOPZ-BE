package kr.co.loopz.authentication.client;

import kr.co.loopz.authentication.dto.response.KakaoResourceServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(
        name = "kakao-oauth-client",
        url  = "${etc.kakao-profile-url}"
)
public interface KakaoResourceClient {

    @GetMapping
    KakaoResourceServerResponse getUserInfo(
            @RequestHeader(AUTHORIZATION) String authorizationHeader
    );

}
