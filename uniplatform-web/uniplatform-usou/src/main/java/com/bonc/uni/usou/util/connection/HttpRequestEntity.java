package com.bonc.uni.usou.util.connection;

/**
 * Created by yedunyao on 2017/8/11.
 */
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpRequestEntity extends HttpEntityEnclosingRequestBase {

    private String method = HttpEnum.GET;

    @Override
    public String getMethod() {
        return method;
    }

    public HttpRequestEntity() {
    }

    public HttpRequestEntity(URI uri) {
        this.setURI(uri);
    }

    public HttpRequestEntity(String method, String uri) {
        this.method = method;
        this.setURI(URI.create(uri));
    }
}
