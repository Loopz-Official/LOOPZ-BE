package kr.co.loopz.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("kr.co.loopz")
public class OpenFeignConfig {
}
