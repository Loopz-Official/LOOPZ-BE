package kr.co.loopz.authentication.converter;

import kr.co.loopz.authentication.dto.request.InternalRegisterRequest;
import kr.co.loopz.authentication.dto.response.GoogleResourceServerResponse;
import kr.co.loopz.authentication.dto.response.InternalRegisterResponse;
import kr.co.loopz.authentication.dto.response.SocialLoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = IGNORE
)
public interface AuthConverter {

    AuthConverter INSTANCE = Mappers.getMapper(AuthConverter.class);

    InternalRegisterRequest toInternalRegisterRequest(GoogleResourceServerResponse response);
    SocialLoginResponse toSocialLoginResponse(InternalRegisterResponse response);

}
