package kr.co.loopz.order.repository;

import kr.co.loopz.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrderId(String orderId);

    Optional<OrderItem> findByOrderIdAndObjectId(String orderId, String objectId);

}
