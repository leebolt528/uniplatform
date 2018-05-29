package com.bonc.uni.usou.util.connection;

import com.bonc.uni.usou.config.UsouHttpConfiguration;
import com.bonc.uni.usou.exception.ConnectionException;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by yedunyao on 2017/9/13.
 */
//@Component
public class HttpRequestUtil {

    @Autowired
    UsouHttpConfiguration usouHttpConfiguration;

    public static RequestConfig REQUESTCONFIG;
	public static final String AUTHINFO = "admin:admin";

    @PostConstruct
    private void init () {
        REQUESTCONFIG = this.usouHttpConfiguration.getRequestConfig();
    }

    /**
     * 发送http请求
     * @param method GET PUT POST DELETE
     * @param header
     * @param config
     * @param url
     * @param body
     * @return
     * @throws ResponseException
     */
    public static String request(String method, Header header, RequestConfig config,
                                 String url, String body) throws ResponseException {

        LogManager.method(method + " request send.");
        LogManager.finer("url----------------------" + url);
        LogManager.finer("body----------------------" + body);

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = HttpClients.createDefault();

            HttpRequestEntity entity = new HttpRequestEntity(
                    method, url);
            entity.setHeader(header);
            entity.setConfig(config);
            entity.setEntity(new StringEntity(body, "UTF-8"));

            response = client.execute(entity);

            HttpEntity resEntity = response.getEntity();
            String resStr = EntityUtils.toString(resEntity, Charset.forName("utf-8"));

            LogManager.method(method + " request send success.");
            return resStr;
        } catch (IOException e) {
            LogManager.Exception(e);
            throw new ConnectionException("Send http " + method + " request failed.");
        } finally {
            closeResponse(response);
            closeHttpClient(client);
        }
    }

	public static String request(String method, Header[] headers, RequestConfig config, String url, String body)
			throws ResponseException {

		LogManager.method(method + " request send.");
		LogManager.finer("url----------------------" + url);
		LogManager.finer("body----------------------" + body);

		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			client = HttpClients.createDefault();

			HttpRequestEntity entity = new HttpRequestEntity(method, url);
			entity.setHeaders(headers);
			entity.setConfig(config);
			entity.setEntity(new StringEntity(body, "UTF-8"));

			response = client.execute(entity);

			HttpEntity resEntity = response.getEntity();
			String resStr = EntityUtils.toString(resEntity, Charset.forName("utf-8"));

            LogManager.method(method + " request send success.");
			return resStr;
		} catch (IOException e) {
			LogManager.Exception(e);
			throw new ConnectionException("Send http " + method + " request failed.");
		} finally {
			closeResponse(response);
			closeHttpClient(client);
		}
	}

    public static String requestWithJson(RequestConfig requestConfig,
                                         String method, String url,
                                         String body) throws ResponseException {
		/* Header header = new BasicHeader("Content-Type",
		        "application/json; charset=UTF-8");
		return request(method, header, requestConfig, url, body);*/
		// TODO
		// authInfo 从配置信息中获取
		return requestWithJson(requestConfig, method, AUTHINFO, url, body);
    }

	public static String requestWithJson(RequestConfig requestConfig, String method, String url,String authinfo,
                                         String body)
			throws ResponseException {
		Header[] headers;// = new Header[2];
        if (StringUtil.isNotEmpty(authinfo) && !":".equals(authinfo)) {
			headers = new Header[2];
            headers[1] = new BasicHeader("Authorization", "Basic "
					+ new String(DatatypeConverter.printBase64Binary((authinfo).getBytes(StandardCharsets.UTF_8))));
		} else {
			headers = new Header[1];
		}
		headers[0] = new BasicHeader("Content-Type", "application/json; charset=UTF-8");

		return request(method, headers, REQUESTCONFIG, url, body);
	}

    public static String requestWithJson(String method, String url, String authInfo,
                                         String body) throws ResponseException {
        return requestWithJson(REQUESTCONFIG, method, url, authInfo, body);
    }

    /**
     * 发送http请求，默认超时时间1min，请求格式json
     * @param method GET PUT POST DELETE
     * @param url
     * @param body
     * @return
     * @throws ResponseException
     */
    public static String requestWithJson(String method, String url,
                                         String body) throws ResponseException {
		/*Header header = new BasicHeader("Content-Type",
		        "application/json; charset=UTF-8");
		return request(method, header, REQUESTCONFIG, url, body);*/
		// TODO
		// authInfo 从配置信息中获取
		return requestWithJson(REQUESTCONFIG, method, AUTHINFO, url, body);
    }

    private static void closeHttpClient(CloseableHttpClient client) {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                LogManager.Exception(e);
            }
        }
    }

    private static void closeResponse(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                LogManager.Exception(e);
            }
        }
    }

}
