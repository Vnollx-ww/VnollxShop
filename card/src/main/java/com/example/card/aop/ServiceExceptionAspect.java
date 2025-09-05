package com.example.card.aop;

import com.example.common.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Aspect
@Component
public class ServiceExceptionAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceExceptionAspect.class);

    /**
     * 处理服务层所有方法
     */
    @Around("execution(* com.example.card.service..*.*(..))")
    public Object handleServiceExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            logger.debug("执行服务方法: {}.{}", className, methodName);
            return joinPoint.proceed();

        } catch (BusinessException e) {
            // 业务异常直接抛出，保持原有异常信息
            logger.warn("业务异常 - {}.{}: {}", className, methodName, e.getMessage());
            throw e;

        } catch (NullPointerException e) {
            // 空指针异常
            logger.error("空指针异常 - {}.{}: {}", className, methodName, e.getMessage(), e);
            throw new BusinessException("系统数据异常，请联系管理员");

        } catch (SQLException e) {
            // 数据库相关异常
            logger.error("数据库异常 - {}.{}: {}", className, methodName, e.getMessage(), e);
            throw new BusinessException("数据库操作失败，请稍后重试");

        } catch (IllegalArgumentException e) {
            // 参数非法异常
            logger.warn("参数异常 - {}.{}: {}", className, methodName, e.getMessage());
            throw new BusinessException("请求参数不合法: " + e.getMessage());

        } catch (RuntimeException e) {
            // 其他运行时异常
            logger.error("运行时异常 - {}.{}: {}", className, methodName, e.getMessage(), e);
            throw new BusinessException("系统运行异常，请联系管理员");

        } catch (Exception e) {
            // 其他 checked exception
            logger.error("系统异常 - {}.{}: {}", className, methodName, e.getMessage(), e);
            throw new BusinessException("系统繁忙，请稍后重试");
        }
    }
}