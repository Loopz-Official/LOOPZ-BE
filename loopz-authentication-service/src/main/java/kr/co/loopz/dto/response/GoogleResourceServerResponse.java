package kr.co.loopz.dto.response;

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
