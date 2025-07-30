package kr.co.loopz.search.apiExternal;

import jakarta.validation.Valid;
import kr.co.loopz.search.dto.request.SearchFilterRequest;
import kr.co.loopz.search.dto.response.BoardResponse;
import kr.co.loopz.search.dto.response.SearchHistoryResponse;
import kr.co.loopz.search.dto.response.ObjectNameResponse;
import kr.co.loopz.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import lombok.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/search/v1")
public class SearchController {

    private final SearchService searchService;

    // 검색 시 상품 이름 자동완성
    @GetMapping("")
    public ResponseEntity<List<ObjectNameResponse>> searchProductNames(@RequestParam String keyword) {

        List<ObjectNameResponse> response= searchService.searchProductNamesByKeyword(keyword);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //최근 검색어 목록 조회
    @GetMapping("/history")
    public ResponseEntity<List<SearchHistoryResponse>> getRecentKeywords(@AuthenticationPrincipal User currentUser) {

        String userId = currentUser.getUsername();

        List<SearchHistoryResponse> recentKeywords = searchService.getRecentSearchKeywords(userId);
        return ResponseEntity.status(HttpStatus.OK).body(recentKeywords);
    }

    // 검색 후 상품 목록 반환
    @GetMapping("/objects")
    public ResponseEntity<BoardResponse> search(
            @AuthenticationPrincipal User currentUser,
            @ModelAttribute @Valid SearchFilterRequest filter
    ) {

        String userId = currentUser != null ? currentUser.getUsername() : null;

        BoardResponse response = searchService.searchAndSaveKeyword(userId, filter);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 최근 검색어 삭제
    @DeleteMapping("/{searchId}")
    public ResponseEntity<Void> deleteHistory(@AuthenticationPrincipal User currentUser,
                                             @PathVariable String searchId) {

        String userId = currentUser.getUsername();

        searchService.deleteHistory(userId, searchId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
