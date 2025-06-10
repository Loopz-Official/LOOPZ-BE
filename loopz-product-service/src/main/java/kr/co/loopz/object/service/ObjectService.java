package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.Likes;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.QLikes;
import kr.co.loopz.object.domain.QObjectEntity;
import kr.co.loopz.object.dto.request.FilterRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.co.loopz.object.Exception.ObjectErrorCode.INVALID_SORT_TYPE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectService {

    private final JPAQueryFactory queryFactory;
    private final LikeRepository likeRepository;
    private final ObjectConverter objectConverter;

    public BoardResponse getBoard(String userId, FilterRequest filter) {

        Slice<ObjectEntity> slice = getFilteredObjects(filter);

        List<ObjectResponse> objects = objectConverter.toObjectResponseList(slice.getContent());

        // 상품 ID 목록 추출
        List<String> objectIds = objects.stream()
                .map(ObjectResponse::objectId)
                .collect(Collectors.toList());

        Map<String, Boolean> likeMap = checkLikedObject(userId, objectIds);

        return objectConverter.toBoardResponse(objects, likeMap, slice.hasNext());


    }

    public Slice<ObjectEntity> getFilteredObjects(FilterRequest filter) {


        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        QObjectEntity object = QObjectEntity.objectEntity; //QueryDSL
        BooleanBuilder builder = new BooleanBuilder();

        // objectType 필터
        if (filter.getObjectTypes() != null && !filter.getObjectTypes().isEmpty()) {
            builder.and(object.objectType.in(filter.getObjectTypes()));
        }

        // objectSize 필터
        if (filter.getObjectSizes() != null && !filter.getObjectSizes().isEmpty()) {
            builder.and(object.objectSize.in(filter.getObjectSizes()));
        }

        // priceMin 필터
        if (filter.getPriceMin() != null) {
            builder.and(object.objectPrice.goe(filter.getPriceMin()));
        }

        // priceMax 필터
        if (filter.getPriceMax() != null) {
            builder.and(object.objectPrice.loe(filter.getPriceMax()));
        }

        // keywords 필터
        if (filter.getKeywords() != null && !filter.getKeywords().isEmpty()) {
            builder.and(object.keywords.any().in(filter.getKeywords()));
        }

        // ExcludeSoldOut false면 모든 상품 조회 (품절 상품 포함)
        // ExcludeSoldOut true면 품절상품 제외
        if (Boolean.TRUE.equals(filter.getExcludeSoldOut())) {
            builder.and(object.soldOut.eq(false));
        }

        if ("popular".equals(filter.getSort())) {
            QLikes like = QLikes.likes;

            // objectId별 좋아요 수 집계
            List<Tuple> likeCountTuples = queryFactory
                    .select(object.objectId, like.count())
                    .from(object)
                    .leftJoin(like).on(like.objectId.eq(object.objectId))
                    .where(builder)
                    .groupBy(object.objectId,object.createdAt)
                    .orderBy(like.count().desc(), object.createdAt.desc())
                    .offset((long) filter.getPage() * filter.getSize())
                    .limit(filter.getSize() + 1)
                    .fetch();

            boolean hasNext = likeCountTuples.size() > filter.getSize();
            if (hasNext) {
                likeCountTuples.remove(likeCountTuples.size() - 1);
            }

            List<String> objectIds = likeCountTuples.stream()
                    .map(t -> t.get(object.objectId))
                    .collect(Collectors.toList());

            if (objectIds.isEmpty()) {
                return new SliceImpl<>(Collections.emptyList(), pageable, false);
            }

            // ObjectEntity 조회
            List<ObjectEntity> objects = queryFactory
                    .selectFrom(object)
                    .where(object.objectId.in(objectIds))
                    .fetch();

            Map<String, ObjectEntity> objectMap = objects.stream()
                    .collect(Collectors.toMap(ObjectEntity::getObjectId, Function.identity()));

            List<ObjectEntity> sortedObjects = objectIds.stream()
                    .map(objectMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return new SliceImpl<>(sortedObjects, pageable, hasNext);

        } else if ("latest".equals(filter.getSort())) {

            // 최신순 정렬
            List<ObjectEntity> content = queryFactory
                    .selectFrom(object)
                    .where(builder)
                    .orderBy(object.createdAt.desc())
                    .offset((long) filter.getPage() * filter.getSize())
                    .limit(filter.getSize() + 1)
                    .fetch();

            boolean hasNext = content.size() > filter.getSize();
            if (hasNext) {
                content.remove(content.size() - 1);
            }

            return new SliceImpl<>(content, pageable, hasNext);
        }
        else throw new ObjectException(INVALID_SORT_TYPE,"popular 또는 latest를 입력해주세요.");

    }


    private Map<String, Boolean> checkLikedObject(String userId, List<String> objectIds) {
        if (userId == null || objectIds == null || objectIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 리포지토리에서 직접 좋아요 여부 조회
        List<Likes> likes = likeRepository.findAllByUserIdAndObjectIdIn(userId, objectIds);

        // 좋아요 누른 objectId만 맵에 true로 표시
        Map<String, Boolean> likeMap = new HashMap<>();
        for (String objectId : objectIds) {
            likeMap.put(objectId, false);
        }
        for (Likes like : likes) {
            likeMap.put(like.getObjectId(), true);
        }

        return likeMap;
    }
}









