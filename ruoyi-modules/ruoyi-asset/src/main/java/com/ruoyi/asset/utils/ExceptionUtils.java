package com.ruoyi.asset.utils;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionUtils {
    /**
     * 开启全局异常捕获
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler(Exception e) {
        System.out.println("全局异常捕获：" + e);
        return "全局异常捕获，错误原因：" + e.getMessage();
    }
}
