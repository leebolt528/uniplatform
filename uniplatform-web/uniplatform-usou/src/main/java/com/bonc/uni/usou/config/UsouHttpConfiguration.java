package com.bonc.uni.usou.config;

import org.apache.http.client.config.RequestConfig;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class UsouHttpConfiguration implements EnvironmentAware {

    private int connectTimeout;
    private int connectRequestTimeout;
    private int socketTimeout;

    private int shortConnectTimeout;
    private int shortConnectRequestTimeout;
    private int shortSocketTimeout;

    private RequestConfig requestConfig;

    private RequestConfig shortRequestConfig;

    @Override
    public void setEnvironment(Environment environment) {
        connectTimeout = Integer.valueOf(
                environment.getProperty("usou.http.connect.timeout"));
        connectRequestTimeout = Integer.valueOf(
                environment.getProperty("usou.http.connect.request.timeout"));
        socketTimeout = Integer.valueOf(
                environment.getProperty("usou.http.socket.timeout"));

        shortConnectTimeout = Integer.valueOf(
                environment.getProperty("usou.http.short.connect.timeout"));
        shortConnectRequestTimeout = Integer.valueOf(
                environment.getProperty("usou.http.short.connect.request.timeout"));
        shortSocketTimeout = Integer.valueOf(
                environment.getProperty("usou.http.short.socket.timeout"));

        requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        shortRequestConfig = RequestConfig.custom()
                .setConnectTimeout(shortConnectTimeout)
                .setConnectionRequestTimeout(shortConnectRequestTimeout)
                .setSocketTimeout(shortSocketTimeout)
                .build();
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getConnectRequestTimeout() {
        return connectRequestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getShortConnectTimeout() {
        return shortConnectTimeout;
    }

    public int getShortConnectRequestTimeout() {
        return shortConnectRequestTimeout;
    }

    public int getShortSocketTimeout() {
        return shortSocketTimeout;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public RequestConfig getShortRequestConfig() {
        return shortRequestConfig;
    }
}
