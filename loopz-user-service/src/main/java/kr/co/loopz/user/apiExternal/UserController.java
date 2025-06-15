package kr.co.loopz.user.apiExternal;

import kr.co.loopz.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/v1")
@Slf4j
public class UserController {

    private final UserService userService;

    @DeleteMapping()
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal User currentUser
            ) {

        log.debug("Received request to delete user with ID: {}", currentUser.getUsername());

        String userId = currentUser.getUsername();
        userService.softDeleteUser(userId);

        return ResponseEntity.noContent().build();
    }

}
