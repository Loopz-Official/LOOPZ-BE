package kr.co.loopz.user.dto.response;

public record UserInternalRegisterResponse(
        String email,
        String userId,
        String loginName,
        String nickName,
        String realName,
        boolean enabled
) {
}
