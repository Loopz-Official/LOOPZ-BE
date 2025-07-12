package kr.co.loopz.object.apiExternal;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.co.loopz.object.dto.request.LikedObjectRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/object/v1/likes")
@RequiredArgsConstructor
@Slf4j
public class ObjectLikeController {

    private final LikeService likeService;

    @PatchMapping("/{objectId}")
    @Operation(summary="좋아요 추가/삭제")
    public ResponseEntity<Void> toggleLike(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String objectId){

        String userId = currentUser.getUsername();
        likeService.toggleLike(userId, objectId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping()
    public ResponseEntity<BoardResponse> getLikedObjects(
            @AuthenticationPrincipal User currentUser,
            @ModelAttribute @Valid LikedObjectRequest likedObjectRequest
    ) {

        String userId = currentUser.getUsername();
        BoardResponse response = likeService.getLikedObjects(userId, likedObjectRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }




}
