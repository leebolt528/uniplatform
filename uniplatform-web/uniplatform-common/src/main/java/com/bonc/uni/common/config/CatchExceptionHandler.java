package com.bonc.uni.common.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonc.uni.common.util.ResultUtil;

@ControllerAdvice
public class CatchExceptionHandler {

	@ResponseBody
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
		String msg = e.getMessage();
        if (msg == null || msg.equals("")) {
            msg = "服务器出错";
        }
		return ResultUtil.error("请求失败", msg);
	}
}
