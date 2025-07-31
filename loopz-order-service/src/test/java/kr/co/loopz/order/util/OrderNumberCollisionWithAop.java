package kr.co.loopz.order.util;

import kr.co.loopz.order.client.ObjectClient;
import kr.co.loopz.order.client.UserClient;
import kr.co.loopz.order.domain.enums.PaymentMethod;
import kr.co.loopz.order.dto.request.ObjectRequest;
import kr.co.loopz.order.dto.request.OrderRequest;
import kr.co.loopz.order.dto.response.InternalObjectResponse;
import kr.co.loopz.order.dto.response.OrderResponse;
import kr.co.loopz.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;



@SpringBootTest
@Slf4j
public class OrderNumberCollisionWithAop {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ObjectClient objectClient;

    @Test
    void testOrderWithRetry() {
        Mockito.when(userClient.existsAddressByUserId(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(objectClient.getObjectListByIds(Mockito.anyList()))
                .thenReturn(List.of(
                        new InternalObjectResponse(
                                "test-object-id", "테스트상품", "테스트 소개",
                                "http://image.url", 10000L, false, 1)
                ));

        String userId = "test-user";
        OrderRequest request = createTestOrderRequest();

        int total = 100000;
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < total; i++) {
            try {
                OrderResponse response = orderService.createOrder(userId, request);
                successCount++;
                log.debug("[{}] 주문 성공: {}", i, response.orderId());
            } catch (Exception e) {
                failCount++;
                log.warn("[{}] 주문 실패: {}", i, e.getMessage());
            }
        }

        log.info("총 요청 수: {}", total);
        log.info("성공 수: {}", successCount);
        log.info("실패 수: {}", failCount);
        log.info("성공률: {}%", (successCount * 100.0) / total);
        log.info("실패률: {}%", (failCount * 100.0) / total);
    }

    private OrderRequest createTestOrderRequest() {
        return new OrderRequest(
                List.of(new ObjectRequest("test-object-id", 1)),
                "test-address-id",
                PaymentMethod.BANK_TRANSFER,
                "테스트",
                true
        );
    }

    /**
     * Client를 Mockito mock 객체로 빈 등록합니다.
     * 테스트 중 Client 의존성 주입 시 이 mock이 사용됩니다.
     */
    @TestConfiguration
    static class MockClientsConfig {
        @Bean
        public UserClient userClient() {
            return Mockito.mock(UserClient.class);
        }

        @Bean
        public ObjectClient objectClient() {
            return Mockito.mock(ObjectClient.class);
        }
    }
}
