package kr.co.loopz.service;

import kr.co.loopz.domain.Product;
import kr.co.loopz.client.UserClient;
import kr.co.loopz.converter.ObjectConverter;
import kr.co.loopz.dto.request.LikeCheckRequest;
import kr.co.loopz.dto.response.BoardResponse;
import kr.co.loopz.dto.response.InternalLikeResponse;
import kr.co.loopz.dto.response.ObjectResponse;
import kr.co.loopz.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectService {

    private final ObjectRepository objectRepository;
    private final ObjectConverter objectConverter;
    private final UserClient userClient;

    public BoardResponse getBoard(String userId,int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<Product> slice = objectRepository.findByOrderByCreatedAtDesc(pageable);

        List<ObjectResponse> objects = objectConverter.toObjectResponseList(slice.getContent());

        // 상품 ID 목록 추출
        List<String> objectIds = objects.stream()
                .map(ObjectResponse::objectId)
                .collect(Collectors.toList());

        Map<String, Boolean> likeMap;

        if (userId != null) {
            // 내부 API 호출
            LikeCheckRequest request = new LikeCheckRequest(objectIds);
            InternalLikeResponse likeResponse = userClient.checkLikes(userId, request);
            likeMap = likeResponse.likes();
        } else {
            // 비로그인 상태면 like 정보 없음
            likeMap = Collections.emptyMap();
        }

        return objectConverter.toBoardResponse(objects, likeMap, slice.hasNext());


    }
}









