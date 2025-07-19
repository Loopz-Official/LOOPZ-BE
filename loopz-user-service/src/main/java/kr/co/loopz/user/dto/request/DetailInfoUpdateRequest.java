package kr.co.loopz.user.dto.request;

import kr.co.loopz.user.domain.enums.Gender;

import java.time.LocalDate;

public record DetailInfoUpdateRequest(
        String nickName,
        LocalDate birthDate,
        Gender gender
) {
}
