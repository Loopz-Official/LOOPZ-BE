package kr.co.loopz.common.redis.service;

import kr.co.loopz.common.redis.entity.RefreshToken;
import kr.co.loopz.common.redis.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String userId, String refreshToken, Long ttl) {

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .ttl(ttl)
                .build();

        refreshTokenRepository.save(token);
    }

    public Optional<RefreshToken> findRefreshToken(String userId) {
        return refreshTokenRepository.findById(userId);
    }

    public void deleteRefreshToken(String userId) {
        refreshTokenRepository.deleteById(userId);
    }

}
