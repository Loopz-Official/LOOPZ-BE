package kr.co.loopz.order.converter;

import kr.co.loopz.order.domain.Order;
import kr.co.loopz.order.domain.OrderItem;
import kr.co.loopz.order.dto.response.InternalObjectResponse;
import kr.co.loopz.order.dto.response.ObjectResponse;
import kr.co.loopz.order.dto.response.OrderListResponse;
import kr.co.loopz.order.dto.response.MyOrderObjectResponse;
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

    default OrderResponse toOrderResponse(
            Order order,
            List<OrderItem> items,
            List<InternalObjectResponse> currentObjects,
            int shippingFee
    ){

        long totalProductPrice = items.stream()
                .mapToLong(orderItem -> {
                    return orderItem.getPurchasePrice() * orderItem.getQuantity();
                })
                .sum();

        List<ObjectResponse> objectResponses = items.stream()
                .map(orderItem -> {
                    InternalObjectResponse itemDetail = currentObjects.stream()
                            .filter(matches -> matches.objectId().equals(orderItem.getObjectId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Object not found for ID: " + orderItem.getObjectId()));
                    return toObjectResponse(itemDetail, orderItem.getQuantity());
                })
                .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getPaymentMethod(),
                objectResponses,
                shippingFee,
                totalProductPrice,
                totalProductPrice + shippingFee
        );
    }

    @Mapping(source = "internalObjectResponse.objectPrice", target = "purchasePrice")
    ObjectResponse toObjectResponse(InternalObjectResponse internalObjectResponse, int quantity);


    default MyOrderObjectResponse toMyOrderObjectResponse(OrderItem item, InternalObjectResponse detail) {
        return new MyOrderObjectResponse(
                item.getObjectId(),
                detail.objectName(),
                item.getStatus(),
                detail.imageUrl(),
                item.getPurchasePrice()* item.getQuantity(),
                item.getQuantity(),
                item.getCreatedAt()
        );
    }

    default OrderListResponse toOrderListResponse(Order order, List<MyOrderObjectResponse> objects) {
        return new OrderListResponse(
                order.getOrderId(),
                objects
        );

    }
}

