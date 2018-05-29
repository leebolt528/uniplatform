package com.bonc.uni.usou.exception;

/**
 * Created by yedunyao on 2017/8/16.
 */
public class ConnectionException extends ResponseException {

    private static final String TYPE = "Connection exception : ";

    public ConnectionException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
