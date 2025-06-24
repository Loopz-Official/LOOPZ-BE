package kr.co.loopz.object.service;

import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.client.UserClient;
import kr.co.loopz.object.domain.Likes;
import kr.co.loopz.object.repository.LikeRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kr.co.loopz.object.Exception.ObjectErrorCode.OBJECT_ID_NOT_FOUND;
import static kr.co.loopz.object.Exception.ObjectErrorCode.USER_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final UserClient userClient;
    private final LikeRepository likeRepository;
    private final ObjectRepository objectRepository;


    @Transactional
    public void toggleLike(String userId, String objectId) {

        // userId 존재여부 확인
        if (!userClient.existsByUserId(userId)) {
            throw new ObjectException(
                    USER_ID_NOT_FOUND, "User with ID not found: "+ userId);
        }

        // objectId 존재 여부 확인
        boolean exists = objectRepository.existsByObjectId(objectId);
        if (!exists) {
            throw new ObjectException(OBJECT_ID_NOT_FOUND, "Object ID not found: " + objectId);
        }

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
}


