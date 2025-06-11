package kr.co.loopz.object.apiExternal;

import jakarta.validation.Valid;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.dto.request.FilterRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.dto.response.DetailResponse;
import kr.co.loopz.object.service.ObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.User;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/object/v1")
public class ObjectController {

    private final ObjectService objectService;

    @GetMapping
    public ResponseEntity<BoardResponse> getMainBoard(
            @AuthenticationPrincipal User currentUser,
            @ModelAttribute @Valid FilterRequest filter
    ) {
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUsername();
            log.debug("userId = " + userId);
        } else {
            log.debug("비로그인 상태로 접근");
        }

        BoardResponse response = objectService.getBoard(userId, filter);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{objectId}")
    public ResponseEntity<DetailResponse> getObjectDetails(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String objectId
    ){
        String userId = currentUser != null ? currentUser.getUsername() : null;

        DetailResponse response = objectService.getObjectDetails(userId, objectId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
