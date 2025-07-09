package kr.co.loopz.user.dto.response;

public record DetailInfoUpdateResponse(
        String userId,
        String email,
        String loginName,
        String realName,
        String nickName,
        boolean enabled
) {
}
