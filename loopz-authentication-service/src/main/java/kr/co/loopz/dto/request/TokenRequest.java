package kr.co.loopz.dto.request;

public record TokenRequest(
        String accessToken,
        String idToken
) {
}
