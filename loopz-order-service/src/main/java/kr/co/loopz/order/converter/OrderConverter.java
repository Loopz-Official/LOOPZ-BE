package kr.co.loopz.order.converter;

import kr.co.loopz.order.domain.Order;
import kr.co.loopz.order.dto.request.OrderRequest;
import kr.co.loopz.order.domain.enums.OrderStatus;
import kr.co.loopz.order.dto.response.ObjectResponse;
import kr.co.loopz.order.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = IGNORE
)
public interface OrderConverter {

    OrderResponse toOrderResponse(
            Order order,
            List<ObjectResponse> objects,
            int shippingFee,
            int totalProductPrice,
            int totalPayment
    );
    }

