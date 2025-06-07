package kr.co.loopz.user.dto.response;

public record NickNameUpdateResponse(
        String userId,
        String email,
        String loginName,
        String realName,
        String nickName,
        boolean enabled
) {
}
