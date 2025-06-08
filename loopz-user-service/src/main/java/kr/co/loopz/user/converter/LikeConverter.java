package kr.co.loopz.user.converter;

import kr.co.loopz.user.dto.response.InternalLikeResponse;
import org.mapstruct.Mapper;

import java.util.Map;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = IGNORE
)
public interface LikeConverter {
    default InternalLikeResponse toInternalLikeResponse(Map<String, Boolean> likes) {
        return new InternalLikeResponse(likes);
    }
}
