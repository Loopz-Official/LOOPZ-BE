package kr.co.loopz.authentication.dto.request;

public record InternalRegisterRequest(
        String email,
        String name,
        String givenName,
        String familyName,
        String picture
) {
}
