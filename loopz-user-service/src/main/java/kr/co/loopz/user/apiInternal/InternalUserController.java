package kr.co.loopz.user.apiInternal;

import kr.co.loopz.user.dto.request.UserInternalRegisterRequest;
import kr.co.loopz.user.dto.response.UserInternalRegisterResponse;
import kr.co.loopz.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/user/v1")
@Slf4j
public class InternalUserController {

    private final UserService userService;

    @PostMapping
    public UserInternalRegisterResponse getOrCreateUser(
            @RequestBody UserInternalRegisterRequest registerRequest) {

        log.debug("Received request to get or create user: {}", registerRequest);
        return userService.getOrCreateUser(registerRequest);
    }

}
