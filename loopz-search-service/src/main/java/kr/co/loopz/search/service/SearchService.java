package kr.co.loopz.search.service;

import kr.co.loopz.search.client.ProductClient;
import kr.co.loopz.search.domain.Search;
import kr.co.loopz.search.dto.response.ObjectNameResponse;
import kr.co.loopz.search.dto.response.SearchHistoryResponse;
import kr.co.loopz.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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




}
