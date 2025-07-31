package kr.co.loopz.search.repository;

import kr.co.loopz.search.domain.Search;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchRepository extends JpaRepository<Search, Long> {

    List<Search> findTop20ByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Search> findBySearchIdAndDeletedAtIsNull(String searchId);

    List<Search> findAllByUserIdAndDeletedAtIsNull(String userId);


}
