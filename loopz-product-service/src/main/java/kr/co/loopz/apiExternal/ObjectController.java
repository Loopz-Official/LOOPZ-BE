package kr.co.loopz.apiExternal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import jakarta.validation.constraints.Min;
import kr.co.loopz.domain.enums.Keyword;
import kr.co.loopz.domain.enums.ObjectSize;
import kr.co.loopz.domain.enums.ObjectType;
import kr.co.loopz.dto.response.BoardResponse;
import kr.co.loopz.service.ObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/object/v1")
@Validated
public class ObjectController {

    private final ObjectService objectService;

    /**
     * 메인 화면 상품 목록 조회 (무한스크롤)
     *
     * @param objectTypes 상품 타입
     * @param objectSizes SMALL/MEDIUM/LARGE
     * @param priceMin 필터 최소 가격
     * @param priceMax 필터 최대 가격
     * @param keywords 키워드
     * @param soldOut 품절여부
     * @param sort 최신순(latest), 인기순(popular)
     * @param page   현재 페이지 번호 (0부터 시작)
     * @param size   한 페이지에 가져올 개수
     * @return BoardResponse (상품 목록 + hasNext)
     */
    @GetMapping
    public ResponseEntity<BoardResponse> getMainBoard(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) Set<ObjectType> objectTypes,
            @RequestParam(required = false) Set<ObjectSize> objectSizes,
            @RequestParam(required = false) Integer priceMin,
            @RequestParam(required = false) Integer priceMax,
            @RequestParam(required = false) Set<Keyword> keywords,
            @RequestParam(required = false) Boolean soldOut,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUsername();
            log.debug("userId = " + userId);
        } else {
            log.debug("비로그인 상태로 접근");
        }

        BoardResponse response = objectService.getBoard(userId, objectTypes, objectSizes, priceMin, priceMax, keywords, soldOut, sort, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
