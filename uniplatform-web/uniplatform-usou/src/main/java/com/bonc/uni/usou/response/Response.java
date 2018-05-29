package com.bonc.uni.usou.response;

//TODO: 返回结果统一接口

/**
 * Created by yedunyao on 2017/9/2.
 */
public class Response {

    public static final String SUCCESS = "200";
    public static final String FAIL = "400";

    public String status;

    public String message;

    public Object data;

    public String url;

    public Response() {
    }

    public Response(String status) {
        this.status = status;
    }

    public Response(String status, String message, Object data, String url) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "response{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
