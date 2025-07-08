package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.Likes;
import kr.co.loopz.object.domain.ObjectImage;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.QLikes;
import kr.co.loopz.object.domain.QObjectEntity;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.co.loopz.object.Exception.ObjectErrorCode.INVALID_SORT_TYPE;
import static kr.co.loopz.object.Exception.ObjectErrorCode.OBJECT_ID_NOT_FOUND;

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
        Slice<ObjectEntity> slice = getFilteredObjects(filter);
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

    public Slice<ObjectEntity> getFilteredObjects(FilterRequest filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        BooleanBuilder builder = buildFilter(filter, QObjectEntity.objectEntity);
        boolean[] hasNextHolder = new boolean[1];

        List<ObjectEntity> result = fetchFilteredObjects(builder, pageable, filter.getSort(), filter.getSize(), hasNextHolder);
        return new SliceImpl<>(result, pageable, hasNextHolder[0]);
    }

    @Transactional
    public BoardResponse searchObjectsByKeyword(String userId, SearchFilterRequest filter) {
        Pageable pageable = PageRequest.of(filter.page(), filter.size());
        BooleanBuilder builder = buildKeywordFilter(filter);

        boolean[] hasNextHolder = new boolean[1];
        List<ObjectEntity> objects = fetchFilteredObjects(builder, pageable, filter.sort(), filter.size(), hasNextHolder);

        return buildBoardResponse(userId, objects, hasNextHolder[0], pageable);
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

    private BooleanBuilder buildKeywordFilter(SearchFilterRequest filter) {
        QObjectEntity object = QObjectEntity.objectEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (filter.keyword() != null && !filter.keyword().isEmpty()) {
            String pattern = "%" + filter.keyword() + "%";
            builder.and(object.objectName.likeIgnoreCase(pattern)
                    .or(object.intro.likeIgnoreCase(pattern)));
        }
        if (Boolean.TRUE.equals(filter.excludeSoldOut())) {
            builder.and(object.soldOut.eq(false));
        }

        return builder;
    }

    private List<ObjectEntity> fetchFilteredObjects(
            BooleanBuilder builder,
            Pageable pageable,
            String sortType,
            int size,
            boolean[] hasNextHolder
    ) {
        QObjectEntity object = QObjectEntity.objectEntity;

        if ("popular".equals(sortType)) {
            QLikes like = QLikes.likes;

            List<Tuple> tuples = queryFactory
                    .select(object, like.count())
                    .from(object)
                    .leftJoin(like).on(like.objectId.eq(object.objectId))
                    .where(builder)
                    .groupBy(object.id, object.createdAt, object.intro, object.objectId,
                            object.objectName, object.objectPrice, object.objectSize,
                            object.objectType, object.soldOut, object.updatedAt)
                    .orderBy(like.count().desc(), object.createdAt.desc())
                    .offset(pageable.getOffset())
                    .limit(size + 1)
                    .fetch();

            hasNextHolder[0] = hasNext(tuples, size);
            return tuples.stream().map(t -> t.get(object)).collect(Collectors.toList());

        } else if ("latest".equals(sortType)) {
            List<ObjectEntity> content = queryFactory
                    .selectFrom(object)
                    .where(builder)
                    .orderBy(object.createdAt.desc())
                    .offset(pageable.getOffset())
                    .limit(size + 1)
                    .fetch();

            hasNextHolder[0] = hasNext(content, size);
            return content;

        } else {
            throw new ObjectException(INVALID_SORT_TYPE, "popular 또는 latest를 입력해주세요.");
        }
    }

    private <T> boolean hasNext(List<T> list, int pageSize) {
        boolean hasNext = list.size() > pageSize;
        if (hasNext) {
            list.remove(list.size() - 1);
        }
        return hasNext;
    }

    private Map<String, String> loadThumbnails(List<String> objectIds) {
        return objectImageRepository.findByObjectIdIn(objectIds).stream()
                .collect(Collectors.groupingBy(
                        ObjectImage::getObjectId,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                ObjectImage::getImageUrl,
                                Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))
                        )
                ));
    }

    private Map<String, Boolean> loadLikeMap(String userId, List<String> objectIds) {
        if (userId == null || objectIds == null || objectIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Likes> likes = likeRepository.findAllByUserIdAndObjectIdIn(userId, objectIds);
        Map<String, Boolean> likeMap = objectIds.stream()
                .collect(Collectors.toMap(Function.identity(), id -> false));

        for (Likes like : likes) {
            likeMap.put(like.getObjectId(), true);
        }

        return likeMap;
    }

    private BoardResponse buildBoardResponse(String userId, List<ObjectEntity> entities, boolean hasNext, Pageable pageable) {
        List<String> objectIds = entities.stream()
                .map(ObjectEntity::getObjectId)
                .collect(Collectors.toList());

        List<ObjectResponse> objects = objectConverter.toObjectResponseList(entities);
        Map<String, String> imageMap = loadThumbnails(objectIds);
        Map<String, Boolean> likeMap = loadLikeMap(userId, objectIds);

        long totalCount = objectRepository.count();
        return objectConverter.toBoardResponse((int) totalCount, objects, imageMap, likeMap, hasNext);
    }

    // 실시간 검색: objectName 기준 Top 10
    public List<ObjectNameResponse> searchObjectsByKeyword(String keyword) {
        List<ObjectEntity> result = objectRepository.findTop10ByObjectNameContainingIgnoreCase(keyword);
        return objectConverter.toObjectNameResponseList(result);
    }
}
