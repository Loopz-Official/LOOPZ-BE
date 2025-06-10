package kr.co.loopz.user.apiInternal;

import kr.co.loopz.user.dto.request.UserInternalRegisterRequest;
import kr.co.loopz.user.dto.response.UserInternalRegisterResponse;
import kr.co.loopz.user.repository.UserRepository;
import kr.co.loopz.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/user")
@Slf4j
public class InternalUserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping
    public UserInternalRegisterResponse getOrCreateUser(
            @RequestBody UserInternalRegisterRequest registerRequest) {

        log.debug("Received request to get or create user: {}", registerRequest);
        return userService.getOrCreateUser(registerRequest);
    }

    @GetMapping("/{userId}/exists")
    public boolean existsByUserId(@PathVariable("userId") String userId) {
        log.debug("Checking existence for userId: {}", userId);
        return userRepository.existsByUserId(userId);
    }

}
