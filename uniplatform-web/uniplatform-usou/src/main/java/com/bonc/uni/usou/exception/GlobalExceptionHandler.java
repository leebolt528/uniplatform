package com.bonc.uni.usou.exception;

import com.bonc.uni.usou.response.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yedunyao on 2017/8/11.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ResponseException.class)
    @ResponseBody
    public Response jsonErrorHandler(HttpServletRequest req, ResponseException e) throws Exception {
        Response r = new Response();
        r.setMessage(e.getMessage());
        r.setStatus(Response.FAIL);
        r.setUrl(req.getRequestURL().toString());
        return r;
    }

}