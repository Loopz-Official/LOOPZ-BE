package kr.co.loopz.order.util;

import java.util.List;

import kr.co.loopz.order.domain.Order;
import kr.co.loopz.order.domain.enums.PaymentMethod;
import kr.co.loopz.order.dto.request.ObjectRequest;
import kr.co.loopz.order.dto.request.OrderRequest;
import kr.co.loopz.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@Slf4j
@DataJpaTest
@RequiredArgsConstructor
public class OrderNumberCollisionTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void collisionTest() {
        int total = 100000;
        int collisions = 0;
        int success = 0;

        for (int i = 0; i < total; i++) {

            try {
                OrderRequest request = new OrderRequest(
                        List.of(new ObjectRequest("test-object-id", 1)),
                        "test-address-id",
                        PaymentMethod.BANK_TRANSFER,
                        "테스트 요청사항",
                        true
                );

                Order order = Order.createOrder(request, "test-user");
                orderRepository.save(order);
                success++;

            } catch (Exception e) {
                collisions++;
                log.warn("[{}] 저장 실패: {}", i, e.toString());
            }
        }


        double rate = (collisions * 100.0) / total;
        log.info("총 요청: {}, 성공: {}, 충돌 수: {}, 충돌률: {}%", total, success, collisions, rate);
    }

}