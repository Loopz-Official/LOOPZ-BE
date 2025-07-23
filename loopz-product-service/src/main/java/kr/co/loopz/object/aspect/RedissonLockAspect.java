package kr.co.loopz.object.aspect;

import kr.co.loopz.object.annotation.RedissonLock;
import kr.co.loopz.object.config.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(kr.co.loopz.object.annotation.RedissonLock)")
    public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLock annotation = method.getAnnotation(RedissonLock.class);

        String lockKey = method.getName() + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), annotation.key());
        RLock rLock = redissonClient.getLock(lockKey);

        try {
            boolean available = rLock.tryLock(annotation.waitTime(), annotation.leaseTime(), MILLISECONDS);
            if (!available) {
                log.debug("락 획득 실패: {}", lockKey);
                return false;
            }
            log.debug("락 획득 성공: {}", lockKey);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new InterruptedException("Lock acquisition interrupted for key: " + lockKey);
        } finally {
            log.debug("락 해제: {}", lockKey);
            rLock.unlock();
        }
    }

}
