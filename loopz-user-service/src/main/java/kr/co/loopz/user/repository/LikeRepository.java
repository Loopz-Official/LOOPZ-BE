package kr.co.loopz.user.repository;

import kr.co.loopz.user.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    @Query("SELECT l.objectId FROM Likes l WHERE l.userId = :userId AND l.objectId IN :objectIds")
    List<String> findLikedObjectIdsByUserIdAndObjectIds(@Param("userId") String userId, @Param("objectIds") List<String> objectIds);

    @Query("SELECT o.objectId FROM Product o WHERE o.objectId IN :ids")
    List<String> findExistingObjectIds(@Param("ids") List<String> ids);


}