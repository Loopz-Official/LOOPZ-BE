package kr.co.loopz.order.aop;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
        maxAttempts = 5,
        backoff = @Backoff(delay = 10)
)
public @interface RetryOrder {

    // 5회 재시도
    int maxAttempts() default 5;
}