package kr.co.loopz.repository;

import kr.co.loopz.dto.response.ObjectResponse;
import kr.co.loopz.domain.Product;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ObjectRepository extends JpaRepository<Product, Long> {

    Slice<Product> findByOrderByCreatedAtDesc(Pageable pageable);
}
