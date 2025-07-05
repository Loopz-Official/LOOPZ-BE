package kr.co.loopz.search.apiExternal;

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



}
