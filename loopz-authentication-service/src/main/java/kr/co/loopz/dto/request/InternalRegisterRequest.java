package kr.co.loopz.dto.request;

import kr.co.loopz.dto.response.GoogleResourceServerResponse;

public record InternalRegisterRequest(
        String email,
        String name,
        String givenName,
        String familyName,
        String picture
) {

    public static InternalRegisterRequest from(GoogleResourceServerResponse response) {
        return new InternalRegisterRequest(
                response.email(),
                response.name(),
                response.givenName(),
                response.familyName(),
                response.picture()
        );
    }

}
