package kr.co.loopz.payment.converter;

import io.portone.sdk.server.payment.PaidPayment;
import kr.co.loopz.payment.domain.enums.OrderStatus;
import kr.co.loopz.payment.saga.event.PaymentCompleteEvent;
import kr.co.loopz.payment.dto.response.InternalOrderResponse;
import kr.co.loopz.payment.dto.response.PaymentCompleteResponse;
import kr.co.loopz.payment.dto.response.PortOneCustomData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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
        return new PaymentCompleteEvent(
                response.paidPayment().getId(),
                response.userId(),
                response.orderId(),
                response.objects()
        );
    }
}
