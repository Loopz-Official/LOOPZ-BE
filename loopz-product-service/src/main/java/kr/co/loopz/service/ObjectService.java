package kr.co.loopz.service;

import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.querydsl.core.types.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.OrderSpecifier;
import kr.co.loopz.Exception.ObjectException;
import kr.co.loopz.domain.ObjectEntity;
import kr.co.loopz.client.UserClient;
import kr.co.loopz.converter.ObjectConverter;
import kr.co.loopz.domain.QObjectEntity;
import kr.co.loopz.domain.enums.Keyword;
import kr.co.loopz.domain.enums.ObjectSize;
import kr.co.loopz.domain.enums.ObjectType;
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
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static kr.co.loopz.Exception.ObjectErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectService {

    private final ObjectRepository objectRepository;
    private final JPAQueryFactory queryFactory;
    private final ObjectConverter objectConverter;
    private final UserClient userClient;

    public BoardResponse getBoard(String userId,
                                  Set<ObjectType> objectTypes,
                                  Set<ObjectSize> objectSizes,
                                  Integer priceMin,
                                  Integer priceMax,
                                  Set<Keyword> keywords,
                                  Boolean soldOut,
                                  String sort, int page, int size) {

        Slice<ObjectEntity> slice = getFilteredObjects( objectTypes, objectSizes, priceMin, priceMax, keywords, soldOut, sort, page, size);

        List<ObjectResponse> objects = objectConverter.toObjectResponseList(slice.getContent());

        // 상품 ID 목록 추출
        List<String> objectIds = objects.stream()
                .map(ObjectResponse::objectId)
                .collect(Collectors.toList());

        Map<String, Boolean> likeMap= checkLikedObject(userId, objectIds);

        return objectConverter.toBoardResponse(objects, likeMap, slice.hasNext());


    }

    public Slice<ObjectEntity> getFilteredObjects( Set<ObjectType> objectTypes,
                                                   Set<ObjectSize> objectSizes,
                                                   Integer priceMin,
                                                   Integer priceMax,
                                                   Set<Keyword> keywords,
                                                   Boolean soldOut,
                                                   String sort, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        QObjectEntity object = QObjectEntity.objectEntity; //QueryDSL
        BooleanBuilder builder = new BooleanBuilder();

        // objectType 필터
        if (objectTypes != null && !objectTypes.isEmpty()) {
            builder.and(object.objectType.in(objectTypes));
        }

        // objectSize 필터
        if (objectSizes != null && !objectSizes.isEmpty()) {
            builder.and(object.objectSize.in(objectSizes));
        }

        // priceMin 필터
        if (priceMin != null) {
            builder.and(object.objectPrice.goe(priceMin));
        }

        // priceMax 필터
        if (priceMax != null) {
            builder.and(object.objectPrice.loe(priceMax));
        }

        // keywords 필터
        if (keywords != null && !keywords.isEmpty()) {
            builder.and(object.keywords.any().in(keywords));
        }

        // soleOut true면 모든 상품 조회 (품절 상품 포함)
        // soldOut false면 품절상품 제외
        if (soldOut != null && !soldOut) {
            builder.and(object.soldOut.eq(false));
        }

        // 정렬 기준 결정
        OrderSpecifier<?>[] orderSpecifiers = toSort(sort, object);

        // 쿼리 실행
        List<ObjectEntity> content = queryFactory
                .selectFrom(object)
                .where(builder)
                .orderBy(orderSpecifiers)
                .offset((long) page * size)
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }


    private OrderSpecifier<?>[] toSort(String sort, QObjectEntity object) {
        if (sort == null || sort.equals("latest")) {
            return new OrderSpecifier<?>[]{object.createdAt.desc()};
        } else if ("popular".equals(sort)) {
            // 좋아요 수 같을 시 최신순 정렬
            return new OrderSpecifier<?>[]{object.likeCount.desc(), object.createdAt.desc()};
        } else {
            throw new ObjectException(INVALID_SORT_TYPE,"popular 혹은 latest를 입력해주세요.");
        }
    }


    private Map<String, Boolean> checkLikedObject(String userId, List<String> objectIds) {
        if (userId != null) {
            LikeCheckRequest request = new LikeCheckRequest(objectIds);
            InternalLikeResponse likeResponse = userClient.checkLikes(userId, request);
            return likeResponse.likes();
        } else {
            return Collections.emptyMap();
        }
    }

    // internal

    @Transactional
    public void updateLikeCount(String objectId, boolean increase) {
        ObjectEntity object = objectRepository.findByObjectId(objectId)
                .orElseThrow(() -> new ObjectException(OBJECT_ID_NOT_FOUND));

        int currentCount = object.getLikeCount();
        if (increase) {
            object.setLikeCount(currentCount + 1);
        } else {
            if (currentCount > 0) {
                object.setLikeCount(currentCount - 1);
            }
        }

        objectRepository.save(object);
    }
}









