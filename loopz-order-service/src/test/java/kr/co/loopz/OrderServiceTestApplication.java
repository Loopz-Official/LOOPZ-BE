package kr.co.loopz;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(scanBasePackages = "kr.co.loopz.order")
@EntityScan("kr.co.loopz.order.domain")
@EnableJpaRepositories("kr.co.loopz.order.repository")
@EnableRetry
public class OrderServiceTestApplication {}