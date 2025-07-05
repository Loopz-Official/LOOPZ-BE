package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.core.instrument.search.Search;
import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.*;
import kr.co.loopz.object.dto.request.FilterRequest;
import kr.co.loopz.object.dto.request.SearchFilterRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.dto.response.DetailResponse;
import kr.co.loopz.object.dto.response.ObjectNameResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static kr.co.loopz.object.Exception.ObjectErrorCode.INVALID_SORT_TYPE;
import static kr.co.loopz.object.Exception.ObjectErrorCode.OBJECT_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectService {

    private final ObjectImageRepository objectImageRepository;
    private final JPAQueryFactory queryFactory;
    private final LikeRepository likeRepository;
    private final ObjectConverter objectConverter;
    private final ObjectRepository objectRepository;

    public BoardResponse getBoard(String userId, FilterRequest filter) {

        Slice<ObjectEntity> slice = getFilteredObjects(filter);

        List<ObjectEntity> entities = slice.getContent();

        List<String> objectIds = entities.stream()
                .map(ObjectEntity::getObjectId)
                .collect(Collectors.toList());

        List<ObjectResponse> objects = objectConverter.toObjectResponseList(entities);

        //image 처리
        List<ObjectImage> objectImages = objectImageRepository.findByObjectIdIn(objectIds);
        Map<String, String> imageMap = objectImages.stream()
                .collect(Collectors.groupingBy(
                        ObjectImage::getObjectId,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                ObjectImage::getImageUrl,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.get(0)        // 첫 번째 이미지 URL만 선택
                                )
                        )
                ));


        Map<String, Boolean> likeMap = checkLikedObject(userId, objectIds);
        long totalCount = objectRepository.count();

        return objectConverter.toBoardResponse(Math.toIntExact(totalCount), objects, imageMap, likeMap, slice.hasNext());

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
                    .select(object, like.count())
                    .from(object)
                    .leftJoin(like).on(like.objectId.eq(object.objectId))
                    .where(builder)
                    .groupBy(object.id,
                            object.createdAt,
                            object.intro,
                            object.objectId,
                            object.objectName,
                            object.objectPrice,
                            object.objectSize,
                            object.objectType,
                            object.soldOut,
                            object.updatedAt)
                    .orderBy(like.count().desc(), object.createdAt.desc())
                    .offset((long) filter.getPage() * filter.getSize())
                    .limit(filter.getSize() + 1)
                    .fetch();

            boolean next = hasNext(likeCountTuples, filter.getSize());

            List<ObjectEntity> orderedObjects = likeCountTuples.stream()
                    .map(tuple -> tuple.get(object))
                    .collect(Collectors.toList());

            return new SliceImpl<>(orderedObjects, pageable, next);

        } else if ("latest".equals(filter.getSort())) {

            // 최신순 정렬
            List<ObjectEntity> content = queryFactory
                    .selectFrom(object)
                    .where(builder)
                    .orderBy(object.createdAt.desc())
                    .offset((long) filter.getPage() * filter.getSize())
                    .limit(filter.getSize() + 1)
                    .fetch();

            boolean next = hasNext(content, filter.getSize());

            return new SliceImpl<>(content, pageable, next);
        } else throw new ObjectException(INVALID_SORT_TYPE, "popular 또는 latest를 입력해주세요.");

    }

    private <T> boolean hasNext(List<T> list, int pageSize) {
        boolean hasNext = list.size() > pageSize;
        if (hasNext) {
            list.remove(list.size() - 1);
        }
        return hasNext;
    }


    // 좋아요 여부 체크
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

    // 제품 상세보기
    public DetailResponse getObjectDetails(String userId, String objectId) {
        ObjectEntity entity = objectRepository.findByObjectId(objectId)
                .orElseThrow(() -> new ObjectException(OBJECT_ID_NOT_FOUND, "Object not found: " + objectId));

        Boolean liked = false;
        if (userId != null) {
            liked = likeRepository.existsByUserIdAndObjectId(userId, objectId);
        }

        List<ObjectImage> images = objectImageRepository.findByObjectId(objectId);
        List<String> imageUrls = images.stream()
                .map(ObjectImage::getImageUrl)
                .collect(Collectors.toList());

        return objectConverter.toDetailResponse(entity, imageUrls, liked);
    }

    public ObjectResponse getObjectById(String objectId) {
        ObjectEntity entity = objectRepository.findByObjectId(objectId)
                .orElseThrow(() -> new ObjectException(OBJECT_ID_NOT_FOUND, "Object not found: " + objectId));

        List<ObjectImage> images = objectImageRepository.findByObjectId(objectId);
        String firstImageUrl = images.isEmpty() ? "" : images.get(0).getImageUrl();

        return new ObjectResponse(
                entity.getObjectId(),
                entity.getObjectName(),
                entity.getIntro(),
                firstImageUrl,
                entity.getObjectPrice(),
                entity.isSoldOut(),
                false,
                entity.getStock()
        );
    }

    public int getStock(String objectId) {
        ObjectEntity object = objectRepository.findByObjectId(objectId)
                .orElseThrow(() -> new ObjectException(OBJECT_ID_NOT_FOUND));
        return object.getDetail().getStock();

    }

    // 주문 후 재고 감소
    @Transactional
    public void decreaseStock(String objectId, int quantity) {
        ObjectEntity object = objectRepository.findByObjectId(objectId)
                .orElseThrow(() -> new ObjectException(OBJECT_ID_NOT_FOUND));

        object.getDetail().decreaseStock(quantity);
    }

    // 검색 시 키워드 포함 상품 제목 반환
    public List<ObjectNameResponse> searchObjectsByKeyword(String keyword) {

        List<ObjectEntity> response = objectRepository.findTop10ByObjectNameContainingIgnoreCase(keyword);

        return objectConverter.toObjectNameResponseList(response);
    }

    @Transactional
    public BoardResponse searchObjectsByKeyword(String userId, SearchFilterRequest filter) {

        Pageable pageable = PageRequest.of(filter.page(), filter.size());

        QObjectEntity object = QObjectEntity.objectEntity;
        BooleanBuilder builder = new BooleanBuilder();

        // 상품 이름과 인트로에서 검색
        if (filter.keyword() != null && !filter.keyword().isEmpty()) {
            String keywordPattern = "%" + filter.keyword() + "%";
            builder.and(object.objectName.likeIgnoreCase(keywordPattern)
                    .or(object.intro.likeIgnoreCase(keywordPattern)));
        }

        // 품절 제외 여부 필터
        if (Boolean.TRUE.equals(filter.excludeSoldOut())) {
            builder.and(object.soldOut.eq(false));
        }

        // 정렬 조건
        if ("popular".equals(filter.sort())) {
            QLikes like = QLikes.likes;

            List<Tuple> likeCountTuples = queryFactory
                    .select(object, like.count())
                    .from(object)
                    .leftJoin(like).on(like.objectId.eq(object.objectId))
                    .where(builder)
                    .groupBy(object.id, object.createdAt, object.intro, object.objectId, object.objectName,
                            object.objectPrice, object.objectSize, object.objectType, object.soldOut, object.updatedAt)
                    .orderBy(like.count().desc(), object.createdAt.desc())
                    .offset((long) filter.page() * filter.size())
                    .limit(filter.size() + 1)
                    .fetch();

            boolean hasNext = hasNext(likeCountTuples, filter.size());

            List<ObjectEntity> orderedObjects = likeCountTuples.stream()
                    .map(tuple -> tuple.get(object))
                    .collect(Collectors.toList());

            return buildBoardResponse(userId, orderedObjects, hasNext, pageable);
        } else if ("latest".equals(filter.sort())) {
            List<ObjectEntity> content = queryFactory
                    .selectFrom(object)
                    .where(builder)
                    .orderBy(object.createdAt.desc())
                    .offset((long) filter.page() * filter.size())
                    .limit(filter.size() + 1)
                    .fetch();

            boolean hasNext = hasNext(content, filter.size());

            return buildBoardResponse(userId, content, hasNext, pageable);
        } else {
            throw new ObjectException(INVALID_SORT_TYPE, "popular 또는 latest를 입력해주세요.");
        }
    }

    private BoardResponse buildBoardResponse(String userId, List<ObjectEntity> entities, boolean hasNext, Pageable pageable) {
        List<String> objectIds = entities.stream()
                .map(ObjectEntity::getObjectId)
                .collect(Collectors.toList());

        List<ObjectResponse> objects = objectConverter.toObjectResponseList(entities);

        List<ObjectImage> objectImages = objectImageRepository.findByObjectIdIn(objectIds);
        Map<String, String> imageMap = objectImages.stream()
                .collect(Collectors.groupingBy(
                        ObjectImage::getObjectId,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                ObjectImage::getImageUrl,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.get(0)
                                )
                        )
                ));

        Map<String, Boolean> likeMap = checkLikedObject(userId, objectIds);

        long totalCount = objectRepository.count();

        return objectConverter.toBoardResponse(
                Math.toIntExact(totalCount),
                objects,
                imageMap,
                likeMap,
                hasNext
        );
    }
}