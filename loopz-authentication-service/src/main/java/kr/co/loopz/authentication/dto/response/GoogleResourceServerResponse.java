package kr.co.loopz.authentication.dto.response;

public record GoogleResourceServerResponse(
        String id,
        String email,
        boolean verifiedEmail,
        String name,
        String givenName,
        String familyName,
        String picture,
        String locale
) {
}
