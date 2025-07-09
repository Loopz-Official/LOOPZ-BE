package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.dsl.CaseBuilder;
import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.Likes;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.QLikes;
import kr.co.loopz.object.domain.QObjectEntity;
import kr.co.loopz.object.domain.ObjectImage;
import java.util.LinkedHashMap;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.co.loopz.object.Exception.ObjectErrorCode.INVALID_SORT_TYPE;

@AllArgsConstructor
@Slf4j
public abstract class AbstractObjectService {

    protected final ObjectImageRepository objectImageRepository;
    protected final LikeRepository likeRepository;
    protected final ObjectRepository objectRepository;
    protected final ObjectConverter objectConverter;
    protected final JPAQueryFactory queryFactory;

    protected List<ObjectEntity> fetchFilteredObjects(
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
                    .orderBy(new CaseBuilder()
                            .when(object.detail.stock.eq(0))
                            .then(1)
                            .otherwise(0).asc(),
                            like.count().desc(), object.createdAt.desc())
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

    protected <T> boolean hasNext(List<T> list, int pageSize) {
        boolean hasNext = list.size() > pageSize;
        if (hasNext) {
            list.remove(list.size() - 1);
        }
        return hasNext;
    }

    protected Map<String, String> loadThumbnails(List<String> objectIds) {
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

    protected Map<String, Boolean> loadLikeMap(String userId, List<String> objectIds) {
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
}


