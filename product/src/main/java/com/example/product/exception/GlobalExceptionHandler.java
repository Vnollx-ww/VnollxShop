package com.example.product.exception;

import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(400,e.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        logger.error("出错啦",e);
        return Result.error(500,"服务器错误，请联系管理员");
    }
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOtherException(Exception e) {
        logger.error("系统错误", e);
        return Result.error(500,"系统繁忙，请稍后重试");
    }
}
