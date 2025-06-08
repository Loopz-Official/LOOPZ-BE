package kr.co.loopz.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.loopz.common.exception.CustomException;
import kr.co.loopz.user.client.ObjectClient;
import kr.co.loopz.user.converter.LikeConverter;
import kr.co.loopz.user.domain.Likes;
import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.response.InternalLikeResponse;
import kr.co.loopz.user.exception.UserException;
import kr.co.loopz.user.repository.LikeRepository;
import kr.co.loopz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import kr.co.loopz.user.exception.UserErrorCode;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static kr.co.loopz.user.exception.UserErrorCode.*;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final LikeConverter likeConverter;
    private final ObjectClient objectClient;

    public InternalLikeResponse checkUserLikes(String userId, List<String> objectIds) {

        // userId 존재여부 확인
        if (!userRepository.existsByUserId(userId)) {
            throw new UserException(USER_NOT_FOUND, String.format("User with ID %s not found", userId));
        }

        List<String> likedObjectIds = likeRepository.findLikedObjectIdsByUserIdAndObjectIds(userId, objectIds);


        Map<String, Boolean> result = new HashMap<>();
        for (String objectId : objectIds) {
            result.put(objectId, likedObjectIds.contains(objectId));
        }

        // objectId 존재여부 확인
        List<String> existingIds = objectClient.findExistingObjectIds(objectIds);
        if (existingIds.size() != objectIds.size()) {
            throw new UserException(OBJECT_ID_NOT_FOUND);
        }

        return likeConverter.toInternalLikeResponse(result);
    }

    @Transactional
    public void toggleLike(String userId, String objectId) {

        // userId 존재여부 확인
        if (!userRepository.existsByUserId(userId)) {
            throw new UserException(USER_NOT_FOUND, String.format("User with ID %s not found", userId));
        }

        // objectId 존재 여부 확인
        boolean exists = objectClient.existsByObjectId(objectId);
        if (!exists) {
            throw new UserException(OBJECT_ID_NOT_FOUND, "Object ID not found: " + objectId);
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

