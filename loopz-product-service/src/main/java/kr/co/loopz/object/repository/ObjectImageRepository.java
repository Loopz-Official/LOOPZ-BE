package kr.co.loopz.object.repository;

import kr.co.loopz.object.domain.ObjectImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObjectImageRepository extends JpaRepository<ObjectImage, Long> {
    List<ObjectImage> findByObjectIdIn(List<String> objectIds);

    Optional<ObjectImage> findByObjectId(String objectId);

}
