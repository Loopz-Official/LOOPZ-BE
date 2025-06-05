package kr.co.loopz.authentication.dto.response;

public record InternalRegisterResponse(
        String email,
        String userId,
        String realName,
        String loginName,
        String nickName,
        boolean enabled
) {
}
