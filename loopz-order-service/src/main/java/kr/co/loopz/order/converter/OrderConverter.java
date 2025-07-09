package kr.co.loopz.order.converter;

import kr.co.loopz.order.domain.Order;
import kr.co.loopz.order.domain.OrderItem;
import kr.co.loopz.order.domain.enums.PaymentMethod;
import kr.co.loopz.order.dto.response.OrderResponse;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = IGNORE
)
public interface OrderConverter {


    OrderResponse toOrderResponse(Order order, List<OrderItem> items, PaymentMethod paymentMethod, int shippingFee);
}

