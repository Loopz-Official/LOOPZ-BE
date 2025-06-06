package kr.co.loopz.user.apiExternal;

import jakarta.validation.Valid;
import kr.co.loopz.user.dto.request.NickNameUpdateRequest;
import kr.co.loopz.user.dto.response.NickNameAvailableResponse;
import kr.co.loopz.user.dto.response.NickNameUpdateResponse;
import kr.co.loopz.user.exception.UserException;
import kr.co.loopz.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/v1/nickname")
@RequiredArgsConstructor
@Slf4j
public class UserNickNameController {

    private final UserService userService;

    @PatchMapping
    public ResponseEntity<NickNameUpdateResponse> updateNickName(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid NickNameUpdateRequest nickNameUpdateRequest) {

        String userId = currentUser.getUsername();
        String nickname = nickNameUpdateRequest.nickname();
        log.debug("Received request to update nickname for userId: {}, with request: {}", userId, nickname);

        NickNameUpdateResponse response = userService.updateNickName(userId, nickname);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/validate")
    public ResponseEntity<NickNameAvailableResponse> validateNickName(
            @RequestParam String nickname) {

        try {
            userService.nickNameValidation(nickname);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new NickNameAvailableResponse(false));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new NickNameAvailableResponse(true));
    }

}
