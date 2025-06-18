package kr.co.loopz.user.dto.request;


import jakarta.validation.constraints.NotBlank;

public record UserInternalRegisterRequest(
        @NotBlank
        String email,
        @NotBlank
        String name,
        String givenName,
        String familyName,
        String picture
) {
}
