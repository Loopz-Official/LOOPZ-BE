package kr.co.loopz.object.apiExternal;


import kr.co.loopz.object.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/object/v1/likes")
@RequiredArgsConstructor
@Slf4j
public class ObjectLikeController {

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
