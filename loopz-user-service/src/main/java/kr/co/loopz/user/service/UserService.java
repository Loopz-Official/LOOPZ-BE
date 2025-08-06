package kr.co.loopz.user.service;

import kr.co.loopz.user.converter.UserConverter;
import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.request.UserInternalRegisterRequest;
import kr.co.loopz.user.dto.response.UserInfoResponse;
import kr.co.loopz.user.dto.response.UserInternalRegisterResponse;
import kr.co.loopz.user.exception.UserException;
import kr.co.loopz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.co.loopz.user.exception.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;


    /**
     * 사용자 정보를 soft delete 처리합니다.
     * @param userId context에서 가져온 사용자 String id
     */
    @Transactional
    public void softDeleteUser(String userId, String reason) {
        UserEntity user = findByUserId(userId);
        user.processWithdrawal(reason);
    }

    /**
     * 내부 응답을 처리하기 위한 메서드
     * auth-service에서 사용자 등록/get 요청을 처리
     * @param registerRequest OAuth 리소스 서버 제공 정보
     * @return Loopz 사용자 정보 응답
     */
    @Transactional
    public UserInternalRegisterResponse getOrCreateUser(UserInternalRegisterRequest registerRequest) {

        UserEntity userEntity = getOrSave(registerRequest);
        return userConverter.toUserInternalRegisterResponse(userEntity);
    }

    /**
     * 사용자 정보를 조회
     * @param userId context에서 가져온 사용자 UUID
     * @return 사용자 정보 응답
     */
    public UserInfoResponse getUserInfo(String userId) {
        UserEntity user = findByUserId(userId);
        return userConverter.toUserInfoResponse(user);
    }


    public UserEntity findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND, String.format("User with ID %s not found", userId)));
    }


    private UserEntity getOrSave(UserInternalRegisterRequest registerRequest) {
        UserEntity userEntity = userRepository.findByEmail(registerRequest.email())
                .orElseGet(() -> UserEntity.from(registerRequest));
        return userRepository.save(userEntity);
    }

}
