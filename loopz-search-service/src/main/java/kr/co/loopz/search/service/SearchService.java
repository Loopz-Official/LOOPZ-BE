package kr.co.loopz.search.service;

import com.querydsl.core.BooleanBuilder;
import jdk.jshell.spi.ExecutionControl;
import kr.co.loopz.common.exception.CustomException;
import kr.co.loopz.search.client.ProductClient;
import kr.co.loopz.search.domain.Search;
import kr.co.loopz.search.dto.request.SearchFilterRequest;
import kr.co.loopz.search.dto.response.BoardResponse;
import kr.co.loopz.search.dto.response.ObjectNameResponse;
import kr.co.loopz.search.dto.response.SearchHistoryResponse;
import kr.co.loopz.search.exception.SearchErrorCode;
import kr.co.loopz.search.exception.SearchException;
import kr.co.loopz.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import static kr.co.loopz.search.exception.SearchErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly=true)
public class SearchService {

    private final ProductClient productClient;
    private final SearchRepository searchRepository;

    // 검색 시 상품 이름 리스트 반환
    public List<ObjectNameResponse> searchProductNamesByKeyword(String keyword) {
        return productClient.findProductNamesByKeyword(keyword);
    }

    public List<SearchHistoryResponse> getRecentSearchKeywords(String userId) {
        List<Search> searches = searchRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId);
        log.info("Searches fetched from DB (size={}): {}", searches.size(), searches.stream().map(Search::getContent).toList());

        // 중복 제거하면서 최대 7개만 리턴
        LinkedHashMap<String, Search> uniqueSearches = new LinkedHashMap<>();
        for (Search s : searches) {
            uniqueSearches.putIfAbsent(s.getContent(), s);
            if (uniqueSearches.size() >= 7) break;
        }

        log.info("Unique keywords to return: {}", uniqueSearches.keySet());

        return uniqueSearches.values().stream()
                .map(s -> new SearchHistoryResponse(s.getSearchId(), s.getContent()))
                .toList();
    }

    @Transactional
    public BoardResponse searchAndSaveKeyword(String userId, SearchFilterRequest filter) {

        // 로그인 안한경우 저장 X
        if (userId == null) {
            return productClient.searchObjects(filter);
        }

        // 검색어 저장
        if (filter.keyword() != null && !filter.keyword().isEmpty()) {
            Search search = Search.builder()
                    .userId(userId)
                    .content(filter.keyword())
                    .build();
            searchRepository.save(search);
            log.info("Saved search keyword '{}' for user '{}'", filter.keyword(), userId);
        }

        // object 서비스 호출
        return productClient.searchObjects(filter);
    }

    @Transactional
    public String deleteHistory(String userId, String searchId) {

        Search search = searchRepository.findBySearchIdAndDeletedAtIsNull(searchId)
                .orElseThrow(() -> new SearchException(SEARCH_ID_NOT_FOUND, "SearchId:" + searchId));

        if (!search.getUserId().equals(userId)) {
            throw new SearchException(USER_ID_NOT_MATCH);
        }

        searchRepository.delete(search);

        return "OK";
    }

    @Transactional
    public String deleteAllHistory(String userId) {

        List<Search> searches = searchRepository.findAllByUserIdAndDeletedAtIsNull(userId);

        if (searches.isEmpty()) {
            throw new SearchException(SEARCH_HISTORY_NOT_FOUND, "userId: " + userId);
        }

        searchRepository.deleteAll(searches);
        return "전체 삭제 성공";
    }
}
