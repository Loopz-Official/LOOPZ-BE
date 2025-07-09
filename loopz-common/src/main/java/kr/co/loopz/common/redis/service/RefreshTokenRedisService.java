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

    public void saveRefreshToken(String userId, String refreshToken, Long ttlInMillis) {

        long ttlInSeconds = ttlInMillis / 1000;

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .ttl(ttlInSeconds)
                .build();

        refreshTokenRepository.save(token);
    }

    public Optional<RefreshToken> findRefreshToken(String userId) {
        return refreshTokenRepository.findById(userId);
    }

    public boolean deleteRefreshToken(String userId) {

        if (!refreshTokenRepository.existsById(userId)) {
            return false;
        }

        refreshTokenRepository.deleteById(userId);
        return true;
    }

}
