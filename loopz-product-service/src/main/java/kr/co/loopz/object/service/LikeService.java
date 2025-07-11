package kr.co.loopz.object.service;

import kr.co.loopz.object.client.UserClient;
import kr.co.loopz.object.domain.Likes;
import kr.co.loopz.object.dto.request.LikedObjectRequest;
import kr.co.loopz.object.dto.response.BoardResponse;
import kr.co.loopz.object.exception.ObjectException;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kr.co.loopz.object.exception.ObjectErrorCode.OBJECT_ID_NOT_FOUND;
import static kr.co.loopz.object.exception.ObjectErrorCode.USER_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final ObjectBoardService objectBoardService;

    private final LikeRepository likeRepository;
    private final ObjectRepository objectRepository;

    private final UserClient userClient;


    /**
     * 사용자가 오브젝트에 좋아요를 토글합니다.
     * 좋아요가 있으면 삭제, 없으면 추가합니다.
     * @param userId 사용자 UUID
     * @param objectId 오브젝트 ID
     */
    @Transactional
    public void toggleLike(String userId, String objectId) {

        checkUserValid(userId);
        checkObjectValid(objectId);

        Optional<Likes> like = likeRepository.findByUserIdAndObjectId(userId, objectId);

        if (like.isPresent()) {
            // 좋아요가 이미 있으면 삭제 (좋아요 취소)
            likeRepository.delete(like.get());

        } else {
            // 좋아요가 없으면 새로 추가
            Likes newLike = Likes.from(userId, objectId);
            likeRepository.save(newLike);
        }
    }


    /**
     * 사용자가 좋아요한 오브젝트 목록을 조회합니다. BoardResponse 형태로 반환, Board Service에서 처리합니다.
     * @param userId 사용자 UUID
     * @param request 페이지 요청 정보 (페이지 번호, 크기)
     * @return BoardResponse 좋아요한 오브젝트 목록, 썸네일, 좋아요 여부, 다음 페이지 여부
     */
    public BoardResponse getLikedObjects(String userId, LikedObjectRequest request) {
        checkUserValid(userId);
        return objectBoardService.getLikedBoardResponse(userId, request.page(), request.size());
    }


    private void checkObjectValid(String objectId) {
        boolean exists = objectRepository.existsByObjectId(objectId);
        if (!exists) {
            throw new ObjectException(OBJECT_ID_NOT_FOUND, "Object ID not found: " + objectId);
        }
    }


    private void checkUserValid(String userId) {
        if (!userClient.existsByUserId(userId)) {
            throw new ObjectException(
                    USER_ID_NOT_FOUND, "User with ID not found: "+ userId);
        }
    }

}


