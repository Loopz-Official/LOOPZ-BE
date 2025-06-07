package kr.co.loopz.repository;

import kr.co.loopz.dto.response.ObjectResponse;
import kr.co.loopz.domain.Product;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ObjectRepository extends JpaRepository<Product, Long> {

    boolean existsByObjectId(String objectId);

    @Query("SELECT o.objectId FROM Product o WHERE o.objectId IN :ids")
    List<String> findExistingObjectIds(List<String> ids);

    Slice<Product> findByOrderByCreatedAtDesc(Pageable pageable);
}
