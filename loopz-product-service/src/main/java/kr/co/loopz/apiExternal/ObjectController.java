package kr.co.loopz.apiExternal;

import jakarta.validation.Valid;
import kr.co.loopz.dto.request.FilterRequest;
import kr.co.loopz.dto.response.BoardResponse;
import kr.co.loopz.service.ObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

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
}
