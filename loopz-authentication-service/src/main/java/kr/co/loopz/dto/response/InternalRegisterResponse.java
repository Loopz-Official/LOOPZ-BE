package kr.co.loopz.dto.response;

public record InternalRegisterResponse(
        String email,
        String userId,
        String realName,
        boolean isEnabled,
        String nickName
) {
}
