package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.QLikes;
import kr.co.loopz.object.domain.QObjectEntity;
import kr.co.loopz.object.dto.request.FilterRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
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

import java.util.*;
import java.util.stream.Collectors;

import static kr.co.loopz.object.Exception.ObjectErrorCode.INVALID_SORT_TYPE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectListService {

    private final ObjectImageRepository objectImageRepository;
    private final LikeRepository likeRepository;
    private final ObjectRepository objectRepository;
    private final ObjectConverter objectConverter;
    private final JPAQueryFactory queryFactory;

    public BoardResponse getBoard(String userId, FilterRequest filter) {

        Slice<ObjectEntity> slice = fetchSlice(filter);
        List<ObjectEntity> entities = slice.getContent();

        List<String> objectIds = entities.stream()
                .map(ObjectEntity::getObjectId)
                .collect(Collectors.toList());
        List<ObjectResponse> objectResponses = objectConverter.toObjectResponseList(entities);

        Map<String, String> imageMap = loadThumbnails(objectIds);
        Map<String, Boolean> likeMap = loadLikeMap(userId, objectIds);

        long totalCount = objectRepository.count();
        return objectConverter.toBoardResponse((int) totalCount, objectResponses, imageMap, likeMap, slice.hasNext());
    }

    private Slice<ObjectEntity> fetchSlice(FilterRequest filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        QObjectEntity qObj = QObjectEntity.objectEntity;
        BooleanBuilder builder = buildFilter(filter, qObj);

        if ("popular".equals(filter.getSort())) {
            return fetchByPopularity(builder, qObj, filter, pageable);
        } else if ("latest".equals(filter.getSort())) {
            return fetchByLatest(builder, qObj, filter, pageable);
        }
        throw new ObjectException(INVALID_SORT_TYPE, "popular 또는 latest를 입력해주세요.");
    }

    private BooleanBuilder buildFilter(FilterRequest filter, QObjectEntity qObj) {
        BooleanBuilder builder = new BooleanBuilder();
        if (filter.getObjectTypes() != null && !filter.getObjectTypes().isEmpty()) {
            builder.and(qObj.objectType.in(filter.getObjectTypes()));
        }
        if (filter.getObjectSizes() != null && !filter.getObjectSizes().isEmpty()) {
            builder.and(qObj.objectSize.in(filter.getObjectSizes()));
        }
        if (filter.getPriceMin() != null) {
            builder.and(qObj.objectPrice.goe(filter.getPriceMin()));
        }
        if (filter.getPriceMax() != null) {
            builder.and(qObj.objectPrice.loe(filter.getPriceMax()));
        }
        if (filter.getKeywords() != null && !filter.getKeywords().isEmpty()) {
            builder.and(qObj.keywords.any().in(filter.getKeywords()));
        }
        if (Boolean.TRUE.equals(filter.getExcludeSoldOut())) {
            builder.and(qObj.soldOut.isFalse());
        }
        return builder;
    }

    private Slice<ObjectEntity> fetchByPopularity(BooleanBuilder builder,
                                                  QObjectEntity qObj,
                                                  FilterRequest filter,
                                                  Pageable pageable) {
        QLikes qLike = QLikes.likes;
        List<Tuple> tuples = queryFactory
                .select(qObj, qLike.count())
                .from(qObj)
                .leftJoin(qLike).on(qLike.objectId.eq(qObj.objectId))
                .where(builder)
                .groupBy(qObj.id,
                         qObj.createdAt,
                         qObj.intro,
                         qObj.objectId,
                         qObj.objectName,
                         qObj.objectPrice,
                         qObj.objectSize,
                         qObj.objectType,
                         qObj.soldOut,
                         qObj.updatedAt)
                .orderBy(qLike.count().desc(), qObj.createdAt.desc())
                .offset((long) filter.getPage() * filter.getSize())
                .limit(filter.getSize() + 1)
                .fetch();

        boolean hasNext = tuples.size() > filter.getSize();
        List<ObjectEntity> content = tuples.stream()
                .limit(filter.getSize())
                .map(tuple -> tuple.get(qObj))
                .collect(Collectors.toList());
        return new SliceImpl<>(content, pageable, hasNext);
    }

    private Slice<ObjectEntity> fetchByLatest(BooleanBuilder builder,
                                              QObjectEntity qObj,
                                              FilterRequest filter,
                                              Pageable pageable) {
        List<ObjectEntity> list = queryFactory
                .selectFrom(qObj)
                .where(builder)
                .orderBy(qObj.createdAt.desc())
                .offset((long) filter.getPage() * filter.getSize())
                .limit(filter.getSize() + 1)
                .fetch();
        boolean hasNext = list.size() > filter.getSize();
        if (hasNext) list.remove(list.size() - 1);
        return new SliceImpl<>(list, pageable, hasNext);
    }

    private Map<String, String> loadThumbnails(List<String> objectIds) {
        return objectImageRepository.findByObjectIdIn(objectIds).stream()
                .collect(Collectors.groupingBy(
                        ObjectImage -> ObjectImage.getObjectId(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                image -> image.getImageUrl(),
                                Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))
                        )
                ));
    }

    private Map<String, Boolean> loadLikeMap(String userId, List<String> objectIds) {
        if (userId == null || objectIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Boolean> likeMap = new HashMap<>();
        for (String id : objectIds) {
            likeMap.put(id, false);
        }
        likeRepository.findAllByUserIdAndObjectIdIn(userId, objectIds)
                .forEach(like -> likeMap.put(like.getObjectId(), true));
        return likeMap;
    }
}