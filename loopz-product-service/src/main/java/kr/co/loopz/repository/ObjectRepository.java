package kr.co.loopz.repository;

import kr.co.loopz.domain.ObjectEntity;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ObjectRepository extends JpaRepository<ObjectEntity, Long> {

    boolean existsByObjectId(String objectId);

    Optional<ObjectEntity> findByObjectId(String objectId);
}
