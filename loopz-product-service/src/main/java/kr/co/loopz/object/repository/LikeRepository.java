package kr.co.loopz.object.repository;

import kr.co.loopz.object.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    @Query("SELECT l.objectId FROM Likes l WHERE l.userId = :userId AND l.objectId IN :objectIds")
    List<String> findLikedObjectIdsByUserIdAndObjectIds(@Param("userId") String userId, @Param("objectIds") List<String> objectIds);

    Optional<Likes> findByUserIdAndObjectId(String userId, String objectId);

    List<Likes> findAllByUserIdAndObjectIdIn(String userId, List<String> objectIds);

    boolean existsByUserIdAndObjectId(String userId, String objectId);

}