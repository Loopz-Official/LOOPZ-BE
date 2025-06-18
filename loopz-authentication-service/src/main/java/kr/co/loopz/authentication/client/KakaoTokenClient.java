package kr.co.loopz.authentication.client;

import kr.co.loopz.authentication.dto.response.KakaoTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "kakao-token-client",
        url  = "${etc.kakao-token-url}",
        configuration = kr.co.loopz.common.config.OpenFeignFormConfig.class
)
public interface KakaoTokenClient {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoTokenResponse getKakaoToken(
            @RequestBody MultiValueMap<String, Object> formData
    );

}
