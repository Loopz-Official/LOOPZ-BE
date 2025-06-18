package kr.co.loopz.object.repository;

import kr.co.loopz.object.domain.ObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ObjectRepository extends JpaRepository<ObjectEntity, Long> {

    boolean existsByObjectId(String objectId);

    Optional<ObjectEntity> findByObjectId(String objectId);

    List<ObjectEntity> findAllByObjectIdIn(List<String> objectIds);
}
