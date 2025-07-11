package kr.co.loopz.object.repository.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.loopz.object.domain.*;
import kr.co.loopz.object.dto.request.enums.SortType;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ObjectQueryRepositoryImpl implements ObjectQueryRepository{

    private final ObjectImageRepository objectImageRepository;

    private final LikeRepository likeRepository;

    private final JPAQueryFactory queryFactory;


    /**
     * 필터링된 오브젝트 목록을 가져옵니다.
     * @param whereClause 필터링 조건을 포함하는 BooleanBuilder
     * @param pageable 페이징 정보
     * @param sortType 정렬 기준 ("popular" 또는 "latest")
     * @param size 페이징 크기
     * @return 필터링된 오브젝트 목록
     */
    @Override
    public List<ObjectEntity> findFilteredObjects(
            BooleanBuilder whereClause,
            Pageable pageable,
            SortType sortType,
            int size
    ) {

        QObjectEntity object = QObjectEntity.objectEntity;

        if (sortType.equals(SortType.popular)) {
            QLikes like = QLikes.likes;

            return queryFactory
                    .selectFrom(object)
                    .leftJoin(like).on(like.objectId.eq(object.objectId))
                    .where(whereClause)
                    .groupBy(object.id, object.createdAt, object.intro, object.objectId,
                             object.objectName, object.objectPrice, object.objectSize,
                             object.objectType, object.updatedAt)
                    .orderBy(zeroStockToBack(object),
                             like.count().desc(),
                             object.createdAt.desc(),
                             object.objectId.asc())
                    .offset(pageable.getOffset())
                    .limit(size + 1)
                    .fetch();
        }

        return queryFactory
                .selectFrom(object)
                .where(whereClause)
                .orderBy(zeroStockToBack(object),
                         object.createdAt.desc(),
                         object.objectId.asc())
                .offset(pageable.getOffset())
                .limit(size + 1)
                .fetch();

    }

    /**
     * 필터링된 오브젝트의 개수를 가져옵니다.
     * @param whereClause 필터링 조건을 포함하는 BooleanBuilder
     * @return 필터링된 오브젝트의 개수
     */
    @Override
    public long countFilteredObjects(BooleanBuilder whereClause) {

        Long count = queryFactory
                .select(QObjectEntity.objectEntity.count())
                .from(QObjectEntity.objectEntity)
                .where(whereClause)
                .fetchOne();

        return count != null ? count : 0L;
    }

    /**
     * 썸네일 이미지 URL을 가져옵니다. DB 1회 조회
     * @param objectIds 찾으려고 하는 오브젝트 UUID 목록
     * @return 오브젝트 UUID를 키로 하고, 해당 오브젝트의 썸네일 이미지 URL을 값으로 가지는 맵
     */
    @Override
    public Map<String, String> fetchThumbnails(List<String> objectIds) {
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

    /**
     * 사용자가 좋아요를 누른 오브젝트 ID 목록을 가져옵니다. DB 1회 조회
     * @param userId 사용자 UUID
     * @param objectIds 찾으려고 하는 오브젝트 UUID 목록
     * @return 오브젝트 UUID를 키로 하고, 해당 오브젝트에 사용자가 좋아요를 눌렀는지 여부를 값으로 가지는 맵
     */
    @Override
    public Map<String, Boolean> fetchLikeMap(String userId, List<String> objectIds) {
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

    /**
     * 재고가 없는 오브젝트를 뒤로 보내는 정렬 조건을 생성합니다.
     * @param object querydsl 오브젝트 엔티티
     * @return 재고가 없는 오브젝트를 뒤로 보내는 정렬 조건
     */
    private OrderSpecifier<Integer> zeroStockToBack(QObjectEntity object) {
        return new CaseBuilder()
                .when(object.detail.stock.eq(0))
                .then(1)
                .otherwise(0).asc();
    }



}
