package kr.co.loopz.user.apiExternal;

import kr.co.loopz.user.dto.request.LikeRequest;
import kr.co.loopz.user.exception.UserErrorCode;
import kr.co.loopz.user.exception.UserException;
import kr.co.loopz.user.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/user/v1/likes")
@RequiredArgsConstructor
@Slf4j
public class UserLikeController {

    private final LikeService likeService;

    @PatchMapping("/{objectId}")
    public ResponseEntity<Void> toggleLike(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String objectId){

        String userId = currentUser.getUsername();
        likeService.toggleLike(userId, objectId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
