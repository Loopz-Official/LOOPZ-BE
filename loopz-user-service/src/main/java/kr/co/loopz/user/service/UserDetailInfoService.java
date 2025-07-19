package kr.co.loopz.user.service;

import kr.co.loopz.user.converter.UserConverter;
import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.request.DetailInfoUpdateRequest;
import kr.co.loopz.user.dto.response.DetailInfoUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserDetailInfoService {

    private final UserService userService;
    private final UserNickNameService userNickNameService;

    private final UserConverter userConverter;


    @Transactional
    public DetailInfoUpdateResponse updateDetailInfo(String userId, DetailInfoUpdateRequest request) {

        UserEntity user = userService.findByUserId(userId);
        checkNullAndUpdateNickname(userId, request);
        checkNullAndUpdateGender(request, user);
        checkNullAndUpdateBirth(request, user);

        return userConverter.toDetailInfoUpdateResponse(user);
        }


    private void checkNullAndUpdateBirth(DetailInfoUpdateRequest request, UserEntity user) {
        if (request.birthDate() != null) {
            user.updateBirthDate(request.birthDate());
        }
    }

    private void checkNullAndUpdateGender(DetailInfoUpdateRequest request, UserEntity user) {
        if (request.gender() != null) {
            user.updateGender(request.gender());
        }
    }

    private void checkNullAndUpdateNickname(String userId, DetailInfoUpdateRequest request) {
        if (request.nickName() != null) {
            userNickNameService.updateNickName(userId, request.nickName());
        }
    }
}
