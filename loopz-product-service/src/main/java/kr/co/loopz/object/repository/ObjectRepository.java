package kr.co.loopz.object.repository;

import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.repository.query.ObjectQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ObjectRepository extends JpaRepository<ObjectEntity, Long>, ObjectQueryRepository {

    boolean existsByObjectId(String objectId);

    Optional<ObjectEntity> findByObjectId(String objectId);

    List<ObjectEntity> findAllByObjectIdIn(List<String> objectIds);

    List<ObjectEntity> findTop10ByObjectNameContainingIgnoreCase(String keyword);
}
