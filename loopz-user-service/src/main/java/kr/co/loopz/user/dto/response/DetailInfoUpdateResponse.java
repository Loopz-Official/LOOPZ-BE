package kr.co.loopz.user.dto.response;

import kr.co.loopz.user.domain.enums.Gender;

import java.time.LocalDate;

public record DetailInfoUpdateResponse(
        String userId,
        String email,
        String loginName,
        String realName,
        String nickName,
        boolean enabled,
        Gender gender,
        LocalDate birthDate
) {
}
