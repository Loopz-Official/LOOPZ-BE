package kr.co.loopz.user.service;

import kr.co.loopz.user.converter.UserConverter;
import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.request.UserInternalRegisterRequest;
import kr.co.loopz.user.dto.response.UserInternalRegisterResponse;
import kr.co.loopz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final UserConverter userConverter;

    public UserInternalRegisterResponse getOrCreateUser(UserInternalRegisterRequest registerRequest) {

        UserEntity userEntity = getOrSave(registerRequest);

        return userConverter.toUserInternalRegisterResponse(userEntity);
    }

    private UserEntity getOrSave(UserInternalRegisterRequest registerRequest) {
        UserEntity userEntity = userRepository.findByEmail(registerRequest.email())
                .orElseGet(() -> UserEntity.from(registerRequest));
        return userRepository.save(userEntity);
    }


}
