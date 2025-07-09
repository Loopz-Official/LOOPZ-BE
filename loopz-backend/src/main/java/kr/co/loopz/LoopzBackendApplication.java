package kr.co.loopz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LoopzBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoopzBackendApplication.class, args);
    }

}
