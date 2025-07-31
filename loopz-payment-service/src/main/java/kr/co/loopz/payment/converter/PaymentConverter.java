package kr.co.loopz.payment.converter;

import io.portone.sdk.server.payment.PaidPayment;
import kr.co.loopz.payment.domain.enums.OrderStatus;
import kr.co.loopz.payment.dto.response.InternalOrderResponse;
import kr.co.loopz.payment.dto.response.PaymentCompleteResponse;
import kr.co.loopz.payment.dto.response.PortOneCustomData;
import kr.co.loopz.payment.saga.event.KafkaPurchasedObject;
import kr.co.loopz.payment.saga.event.PaymentCompleteEvent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = IGNORE
)
public interface PaymentConverter {

    PaymentConverter INSTANCE = Mappers.getMapper(PaymentConverter.class);


    default PaymentCompleteResponse toPaymentCompleteResponse(
            PortOneCustomData customData,
            InternalOrderResponse orderResponse,
            PaidPayment paidPayment
    ) {
        return new PaymentCompleteResponse(
                customData.userId(),
                customData.orderId(),
                paidPayment,
                paidPayment.getMethod(),
                OrderStatus.ORDERED,
                orderResponse.objects()
        );
    }

    default PaymentCompleteEvent toPaymentCompleteEvent(PaymentCompleteResponse response){

        List<KafkaPurchasedObject> objects = response.objects().stream()
                .map(object -> new KafkaPurchasedObject(
                        object.objectId(),
                        object.objectName(),
                        object.imageUrl(),
                        object.purchasePrice().intValue(),
                        object.quantity()
                )).toList();

        return new PaymentCompleteEvent(
                response.paidPayment().getId(),
                response.userId(),
                response.orderId(),
                objects
        );
    }


}
