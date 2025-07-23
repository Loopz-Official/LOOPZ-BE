package kr.co.loopz.object.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface RedissonLock {
    String key();
    long waitTime() default 5000L;
    long leaseTime() default 2000L;
}
