package kr.co.loopz.authorization.config;

import kr.co.loopz.authorization.filter.JwtAuthorizationFilter;
import kr.co.loopz.authorization.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity()
@RequiredArgsConstructor
public class AuthorizationSecurityConfig {

    private final CorsConfig corsConfig;

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    /**
     * authorization 모듈에서 인가 작업만 수행
     * 인증은 loopz-authentication-service 모듈에서 수행
     */

    private static final String[] WHITE_LIST = {
            "/actuator/health",
            "/error",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/auth/v1/login/google",
            "/auth/v1/login/kakao",
            "/internal/**",
            "/object/**"
    };


    @Bean
    public SecurityFilterChain authorizationFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

                // cors
                .cors(cors -> cors.configurationSource(corsConfig.apiCorsConfigurationSource()))

                // 경로별 인가
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated()
                )

                // 인가 필터 설정
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                // 필터 예외 설정
                .addFilterBefore(jwtExceptionFilter, jwtAuthorizationFilter.getClass());

        return http.build();

    }


}
