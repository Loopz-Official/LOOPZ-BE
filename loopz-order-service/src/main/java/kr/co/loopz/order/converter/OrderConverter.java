package kr.co.loopz.order.converter;

import kr.co.loopz.order.domain.Order;
import kr.co.loopz.order.domain.OrderItem;
import kr.co.loopz.order.dto.response.*;
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


    default PurchasedObjectResponse toPurchasedObjectResponse(OrderItem item, InternalObjectResponse detail) {
        return new PurchasedObjectResponse(
                item.getObjectId(),
                detail.objectName(),
                item.getStatus(),
                detail.intro(),
                detail.imageUrl(),
                item.getPurchasePrice(),
                item.getQuantity(),
                item.getCreatedAt()
        );
    }

    default List<PurchasedObjectResponse> toPurchasedObjectResponseList(
            List<OrderItem> orderItems,
            List<InternalObjectResponse> objectDetails
    ) {
        return orderItems.stream()
                .map(item -> {
                    InternalObjectResponse detail = objectDetails.stream()
                            .filter(obj -> obj.objectId().equals(item.getObjectId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Object not found for ID: " + item.getObjectId()));
                    return toPurchasedObjectResponse(item, detail);
                })
                .toList();
    }

    default OrderListResponse toOrderListResponse(Order order, List<PurchasedObjectResponse> objects) {
        return new OrderListResponse(
                order.getOrderId(),
                objects
        );

    }

    default OrderDetailResponse toObjectDetailResponse(
            Order order,
            List<OrderItem> orderItems,
            List<InternalObjectResponse> objectDetails,
            InternalAddressResponse address,
            int shippingFee,
            long totalProductPrice,
            long totalPayment)
    {
        return new OrderDetailResponse(
                order.getOrderId(),
                order.getOrderNumber(),
                order.getPaymentMethod(),
                toPurchasedObjectResponseList(orderItems, objectDetails),
                address,
                shippingFee,
                totalProductPrice,
                totalPayment
        );
    }

}

