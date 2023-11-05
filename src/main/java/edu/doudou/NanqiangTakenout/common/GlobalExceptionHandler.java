package edu.doudou.NanqiangTakenout.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理类
 */

@Slf4j
@ControllerAdvice(annotations = {RestController.class})  //在声明 ***注解的Controller上检查异常
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 捕获到 SQLIntegrityConstraintViolationException.class 异常时的处理方法
     * @param exception
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Res<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.error(exception.getMessage());

        /*
        判断是那个键重复的方法.
         */
        if(exception.getMessage().contains("Duplicate entry")){
            String[] split = exception.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return Res.error(msg);
        }

        return Res.error("未知错误");
    }


    @ExceptionHandler(CustomException.class)
    public Res<String> exceptionHandler(CustomException exception){
        log.error(exception.getMessage());

        return Res.error(exception.getMessage());
    }


}
