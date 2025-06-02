package kr.co.loopz.authentication.dto.response;

public record InternalRegisterResponse(
        String email,
        String userId,
        String realName,
        boolean isEnabled,
        String nickName
) {
}
