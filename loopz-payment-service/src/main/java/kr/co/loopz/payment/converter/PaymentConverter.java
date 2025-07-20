package kr.co.loopz.payment.converter;

import io.portone.sdk.server.payment.PaymentMethod;
import kr.co.loopz.payment.domain.enums.OrderStatus;
import kr.co.loopz.payment.dto.response.InternalOrderResponse;
import kr.co.loopz.payment.dto.response.PaymentCompleteResponse;
import kr.co.loopz.payment.dto.response.PortOneCustomData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.ReportingPolicy.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = IGNORE
)
public interface PaymentConverter {

    PaymentConverter INSTANCE = Mappers.getMapper(PaymentConverter.class);


    default PaymentCompleteResponse toPaymentCompleteResponse(
            PortOneCustomData customData,
            InternalOrderResponse orderResponse,
            PaymentMethod method
    ) {
        return new PaymentCompleteResponse(
                method,
                OrderStatus.ORDERED,
                orderResponse.objects()
        );
    }

}
