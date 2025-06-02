package kr.co.loopz.user.dto.response;

import kr.co.loopz.user.domain.UserEntity;

public record UserInternalRegisterResponse(
        String email,
        String userId,
        String realName,
        boolean isEnabled,
        String nickName
) {

    public static UserInternalRegisterResponse from(UserEntity user) {
        return new UserInternalRegisterResponse(
                user.getEmail(),
                user.getUserId(),
                user.getRealName(),
                user.isEnabled(),
                user.getNickName()
        );
    }

}
