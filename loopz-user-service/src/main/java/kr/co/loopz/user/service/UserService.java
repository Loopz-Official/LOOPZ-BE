package kr.co.loopz.user.service;

import com.vane.badwordfiltering.BadWordFiltering;
import kr.co.loopz.user.converter.UserConverter;
import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.request.UserInternalRegisterRequest;
import kr.co.loopz.user.dto.response.NickNameUpdateResponse;
import kr.co.loopz.user.dto.response.UserInternalRegisterResponse;
import kr.co.loopz.user.exception.UserException;
import kr.co.loopz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.co.loopz.user.exception.UserErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final BadWordFiltering badWordFiltering = new BadWordFiltering();

    @Transactional
    public UserInternalRegisterResponse getOrCreateUser(UserInternalRegisterRequest registerRequest) {

        UserEntity userEntity = getOrSave(registerRequest);

        return userConverter.toUserInternalRegisterResponse(userEntity);
    }

    @Transactional
    public NickNameUpdateResponse updateNickName(String userId, String nickname) {

        UserEntity user = findByUserId(userId);

        nickNameValidation(nickname);
        user.updateNickName(nickname);

        return userConverter.toNickNameUpdateResponse(user);
    }

    /**
     * 닉네임 유효성 검사 및 중복 검사
     * @param nickname 닉네임
     * @throws UserException 닉네임이 중복되는 경우
     * @throws UserException 닉네임 길이가 2자 미만이거나 20자 초과인 경우
     * @param nickname
     */
    public void nickNameValidation(String nickname) {

        checkDuplicate(nickname);
        checkLength(nickname);
//        checkFormat(nickname);
        checkAllowed(nickname);

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


    private void checkLength(String nickname) {
        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new UserException(NICKNAME_INVALID, "닉네임은 2자 이상 20자 이하로 입력해주세요.");
        }
    }


    private void checkDuplicate(String nickname) {
        if (userRepository.existsByNickName(nickname)) {
            throw new UserException(NICKNAME_DUPLICATED);
        }
    }


    private void checkAllowed(String nickname) {
        if (badWordFiltering.check(nickname)) {
            throw new UserException(NICKNAME_INVALID, "닉네임에 부적절한 단어가 포함되어 있습니다.");
        }
        if (badWordFiltering.blankCheck(nickname)) {
            throw new UserException(NICKNAME_INVALID, "닉네임에 부적절한 단어가 포함되어 있습니다.");
        }
    }


}
