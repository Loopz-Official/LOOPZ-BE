package kr.co.loopz.user.dto.response;

public record UserInternalRegisterResponse(
        String email,
        String userId,
        String realName,
        boolean enabled,
        String nickName
) {
}
