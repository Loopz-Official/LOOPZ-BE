package kr.co.loopz.user.service;

import kr.co.loopz.common.exception.CustomException;
import kr.co.loopz.user.converter.LikeConverter;
import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.response.InternalLikeResponse;
import kr.co.loopz.user.exception.UserException;
import kr.co.loopz.user.repository.LikeRepository;
import kr.co.loopz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import kr.co.loopz.user.exception.UserErrorCode;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final LikeConverter likeConverter;

    public InternalLikeResponse checkUserLikes(String userId, List<String> objectIds) {

        // userId 존재여부 확인
        if (!userRepository.existsByUserId(userId)) {
            throw new UserException(UserErrorCode.USER_ID_NOT_FOUND);
        }

        List<String> likedObjectIds = likeRepository.findLikedObjectIdsByUserIdAndObjectIds(userId, objectIds);


        Map<String, Boolean> result = new HashMap<>();
        for (String objectId : objectIds) {
            result.put(objectId, likedObjectIds.contains(objectId));
        }

        // objectId 존재여부 확인
        List<String> existingIds = likeRepository.findExistingObjectIds(objectIds);
        if (existingIds.size() != objectIds.size()) {
            throw new UserException(UserErrorCode.OBJECT_ID_NOT_FOUND);
        }

        return likeConverter.toInternalLikeResponse(result);
    }

}
