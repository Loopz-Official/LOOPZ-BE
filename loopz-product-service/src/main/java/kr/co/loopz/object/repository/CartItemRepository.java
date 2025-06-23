package kr.co.loopz.object.repository;

import kr.co.loopz.object.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndObjectId(String cartId, String objectId);

    List<CartItem> findByCartId(String cartId);

    List<CartItem> findByCartIdAndObjectIdIn(String cartId, List<String> objectIds);

    boolean existsByCartIdAndObjectId(String cartId, String objectId);

    @Query("SELECT COUNT(DISTINCT c.objectId) FROM CartItem c WHERE c.cartId = :cartId")
    int countDistinctObjectByCartId(@Param("cartId") String cartId);
}
