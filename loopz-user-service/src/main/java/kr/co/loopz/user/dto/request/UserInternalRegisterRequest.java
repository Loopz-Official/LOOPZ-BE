package kr.co.loopz.user.dto.request;


public record UserInternalRegisterRequest(
        String email,
        String name,
        String givenName,
        String familyName,
        String picture
) {
}
