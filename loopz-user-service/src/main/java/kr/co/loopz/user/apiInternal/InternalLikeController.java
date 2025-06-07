package kr.co.loopz.user.apiInternal;

import kr.co.loopz.user.dto.request.LikeCheckRequest;
import kr.co.loopz.user.dto.response.InternalLikeResponse;
import kr.co.loopz.user.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/likes")
public class InternalLikeController {

    private final LikeService likeService;


    @PostMapping("")
    public InternalLikeResponse checkLikes(
            @RequestHeader String userId,
            @RequestBody LikeCheckRequest request
    ) {
        return likeService.checkUserLikes(userId, request.objectIds());
    }
}
