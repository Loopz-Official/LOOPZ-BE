package kr.co.loopz.search.service;

import com.querydsl.core.BooleanBuilder;
import kr.co.loopz.search.client.ProductClient;
import kr.co.loopz.search.domain.Search;
import kr.co.loopz.search.dto.request.SearchFilterRequest;
import kr.co.loopz.search.dto.response.BoardResponse;
import kr.co.loopz.search.dto.response.ObjectNameResponse;
import kr.co.loopz.search.dto.response.SearchHistoryResponse;
import kr.co.loopz.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

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
        List<Search> searches = searchRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);

        // 중복 제거하면서 최대 5개만 리턴
        LinkedHashSet<String> uniqueKeywords = new LinkedHashSet<>();
        for (Search s : searches) {
            uniqueKeywords.add(s.getContent());
            if (uniqueKeywords.size() >= 5) break;
        }

        return uniqueKeywords.stream()
                .map(SearchHistoryResponse::new)
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
}
