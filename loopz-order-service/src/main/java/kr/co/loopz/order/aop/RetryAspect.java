package kr.co.loopz.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RetryAspect {

    @Around("@annotation(retryOrder)")
    public Object retry(ProceedingJoinPoint pjp, RetryOrder retryOrder) throws Throwable {
        int maxAttempts = retryOrder.maxAttempts();
        int attempts = 0;

        while (true) {
            try {
                attempts++;
                log.info("[재시도 AOP] 시도 {} / 최대 {}", attempts, maxAttempts);
                return pjp.proceed();
            } catch (DataIntegrityViolationException ex) {
                log.warn("[재시도 AOP] 예외 발생: {} (시도 {})", ex.getMessage(), attempts);
                attempts++;
                if (attempts >= maxAttempts) {
                    log.error("[재시도 AOP] 최대 재시도 초과");
                    throw ex;
                }
            }
        }
    }
}
