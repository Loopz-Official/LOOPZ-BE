package kr.co.loopz.apiExternal;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.loopz.dto.response.BoardResponse;
import kr.co.loopz.service.ObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.User;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/object/v1")
public class ObjectController {

    private final ObjectService objectService;

    /**
     * 메인 화면 상품 목록 조회 (무한스크롤)
     *
     * @param page   현재 페이지 번호 (0부터 시작)
     * @param size   한 페이지에 가져올 개수
     * @return BoardResponse (상품 목록 + hasNext)
     */
    @GetMapping
    public ResponseEntity<BoardResponse> getMainBoard(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUsername();
            System.out.println("userId = " + userId);
        } else {
            System.out.println("비로그인 상태로 접근");
        }

        BoardResponse response = objectService.getBoard(userId, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
