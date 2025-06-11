package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.loopz.object.domain.*;
import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.dto.request.FilterRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
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

    private final ObjectImageRepository objectImageRepository;
    private final JPAQueryFactory queryFactory;
    private final LikeRepository likeRepository;
    private final ObjectConverter objectConverter;

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

        return objectConverter.toBoardResponse(objects, imageMap, likeMap, slice.hasNext());

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
        }
        else throw new ObjectException(INVALID_SORT_TYPE,"popular 또는 latest를 입력해주세요.");

    }

    private <T> boolean hasNext(List<T> list, int pageSize) {
        boolean hasNext = list.size() > pageSize;
        if (hasNext) {
            list.remove(list.size() - 1);
        }
        return hasNext;
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









