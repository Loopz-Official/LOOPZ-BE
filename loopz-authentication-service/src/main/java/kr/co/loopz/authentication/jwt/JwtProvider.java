package kr.co.loopz.authentication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration.access}")
    private Long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${jwt.expiration.refresh}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;

    private SecretKey secretKey;

    @PostConstruct
    protected void initSecretKey() {
        this.secretKey = new SecretKeySpec(secret.getBytes(UTF_8),
                                           Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    public String generateAccessToken(Authentication authentication, String userId) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME, "access", userId);
    }

    public String generateRefreshToken(Authentication authentication, String userId) {
        return generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME, "refresh", userId);
    }

    private String generateToken(Authentication authentication, Long expirationMs, String category, String userId) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userId)
                .claim("category", category)
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }


    public Authentication getAuthenticationFromUserId(String userId) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        User principal = new User(userId, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }


    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


}
