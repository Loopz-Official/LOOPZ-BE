package kr.co.loopz.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.loopz.exception.TokenException;
import kr.co.loopz.jwt.JwtProvider;
import kr.co.loopz.redis.entity.RefreshToken;
import kr.co.loopz.redis.service.RefreshTokenRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static kr.co.loopz.constants.SecurityConstants.TOKEN_PREFIX;
import static kr.co.loopz.exception.SecurityErrorCode.REFRESH_EXPIRED;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = extractAccessTokenFromHeader(request);

        if (accessToken == null || accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Access token from request: {}", accessToken);

        validateAndReissue(response, accessToken);
        setAuthentication(accessToken);
        filterChain.doFilter(request, response);

    }

    private String extractAccessTokenFromHeader(HttpServletRequest request) {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.replace(TOKEN_PREFIX, "");
        }

        return null;
    }


    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private void validateAndReissue(HttpServletResponse response, String accessToken) {
        if (!jwtProvider.validateToken(accessToken)) {

            String userId = jwtProvider.getSubject(accessToken);
            Optional<RefreshToken> optionalRefresh = refreshTokenRedisService.findRefreshToken(userId);
            // refresh 만료 - redis 존재하지 않음
            checkRefreshExpire(optionalRefresh);

            // refresh redis에 존재하고 유효
            reissueAccess(response, optionalRefresh);
        }
    }


    private void checkRefreshExpire(Optional<RefreshToken> optionalRefresh) {
        if (optionalRefresh.isEmpty()) {
            log.debug("Refresh token is not found. Redirect to login page.");
            throw new TokenException(REFRESH_EXPIRED);
        }
    }

    private void reissueAccess(HttpServletResponse response, Optional<RefreshToken> optionalRefresh) {
        String refreshToken = optionalRefresh.get().getRefreshToken();
        if (jwtProvider.validateToken(refreshToken)) {
            // 재발급
            String newAccessToken = jwtProvider.reissueWithRefresh(refreshToken);
            response.setHeader(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + newAccessToken);
        }
    }


}
