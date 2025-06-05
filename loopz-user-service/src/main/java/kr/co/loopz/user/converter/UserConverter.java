package kr.co.loopz.user.converter;

import kr.co.loopz.user.domain.UserEntity;
import kr.co.loopz.user.dto.response.UserInternalRegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.ReportingPolicy.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = IGNORE
)
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    UserInternalRegisterResponse toUserInternalRegisterResponse(UserEntity userEntity);

}
