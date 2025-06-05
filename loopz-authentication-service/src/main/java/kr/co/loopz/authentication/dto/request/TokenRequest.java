package kr.co.loopz.authentication.dto.request;

public record TokenRequest(
        String accessToken,
        String idToken
) {
}
