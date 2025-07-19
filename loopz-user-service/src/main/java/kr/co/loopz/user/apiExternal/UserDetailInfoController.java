package kr.co.loopz.user.apiExternal;

import jakarta.validation.Valid;
import kr.co.loopz.user.dto.request.DetailInfoUpdateRequest;
import kr.co.loopz.user.dto.request.NickNameUpdateRequest;
import kr.co.loopz.user.dto.response.DetailInfoUpdateResponse;
import kr.co.loopz.user.dto.response.NickNameAvailableResponse;
import kr.co.loopz.user.exception.UserException;
import kr.co.loopz.user.service.UserDetailInfoService;
import kr.co.loopz.user.service.UserNickNameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
@Slf4j
public class UserDetailInfoController {

    private final UserNickNameService userNickNameService;
    private final UserDetailInfoService userDetailInfoService;

    @PatchMapping("/nickname")
    public ResponseEntity<DetailInfoUpdateResponse> updateNickName(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid NickNameUpdateRequest nickNameUpdateRequest)
    {

        String userId = currentUser.getUsername();
        String nickname = nickNameUpdateRequest.nickname();
        log.debug("Received request to update nickname for userId: {}, with request: {}", userId, nickname);

        DetailInfoUpdateResponse response = userNickNameService.updateNickName(userId, nickname);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/nickname/validate")
    public ResponseEntity<NickNameAvailableResponse> validateNickName(
            @RequestParam String nickname)
    {
        try {
            userNickNameService.nickNameValidation(nickname);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new NickNameAvailableResponse(false));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new NickNameAvailableResponse(true));
    }

    @PatchMapping("/detail")
    public ResponseEntity<DetailInfoUpdateResponse> updateDetail(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid DetailInfoUpdateRequest request
    ) {

        String userId = currentUser.getUsername();

        DetailInfoUpdateResponse response = userDetailInfoService.updateDetailInfo(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
