package kr.co.loopz.object.service;

import com.querydsl.core.BooleanBuilder;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.dto.request.enums.SortType;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 조건을 받아 응답을 완성하는 서비스입니다.
 * 각 서비스에서 사용자 요청과 비즈니스 로직에 맞는 where절을 생성해서 호출합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ObjectBoardService {


    private final ObjectRepository objectRepository;
    private final ObjectConverter objectConverter;

    /**
     * 사용자 ID와 필터를 기반으로 오브젝트 목록을 조회하고, BoardResponse 형태로 반환합니다.
     * @param userId 사용자 UUID
     * @param whereClause 쿼리 조건을 구성하는 BooleanBuilder 객체
     * @return BoardResponse 오브젝트 목록, 썸네일, 좋아요, 다음 페이지 여부
     */
    public BoardResponse getBoardResponse(String userId, BooleanBuilder whereClause, int page, int size, SortType sort) {

        Pageable pageable = PageRequest.of(page, size);

        List<ObjectEntity> objects = objectRepository.findFilteredObjects(whereClause, pageable, sort, size);

        boolean hasNext = hasNext(objects, size);

        List<String> objectIds = objects.stream()
                .map(ObjectEntity::getObjectId)
                .toList();

        Map<String, String> imageMap = objectRepository.fetchThumbnails(objectIds);
        Map<String, Boolean> likeMap = objectRepository.fetchLikeMap(userId, objectIds);
        long totalCount = objectRepository.countFilteredObjects(whereClause);
        List<ObjectResponse> objectResponseList = objectConverter.toObjectResponseList(objects);

        return objectConverter.toBoardResponse((int) totalCount, objectResponseList, imageMap, likeMap, hasNext);

    }


    /**
     * 페이지네이션을 위해 다음 페이지가 있는지 확인합니다.
     * @param list 조회된 오브젝트 목록
     * @param pageSize 페이지 크기
     * @return 다음 페이지가 있는지 여부
     */
    public <T> boolean hasNext(List<T> list, int pageSize) {
        boolean hasNext = list.size() > pageSize;
        if (hasNext) {
            list.remove(list.size() - 1);
        }
        return hasNext;
    }

}
