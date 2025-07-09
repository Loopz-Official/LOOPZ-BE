package kr.co.loopz.user.service;

import kr.co.loopz.user.converter.UserConverter;
import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.request.AgreeTermsRequest;
import kr.co.loopz.user.dto.response.AgreeTermsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class UserTermsService {

    private final UserService userService;
    private final UserConverter userConverter;

    @Transactional
    public AgreeTermsResponse agreeTerms(String userId, AgreeTermsRequest request) {

        UserEntity user = userService.findByUserId(userId);

        user.updateTerms(
                request.over14(),
                request.agreedServiceTerms(),
                request.agreedMarketing(),
                request.agreedEventSMS()
        );

        return userConverter.toAgreeTermsResponse(user);
    }
}
