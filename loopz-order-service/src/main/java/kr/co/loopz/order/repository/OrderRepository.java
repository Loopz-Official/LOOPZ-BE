package kr.co.loopz.order.repository;

import kr.co.loopz.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Order> findByOrderIdAndUserId(String orderId, String userId);

    Optional<Order> findByOrderNumberAndUserId(String orderNumber, String userId);

    Optional<Order> findByOrderId(String orderId);

}
