package io.yule.huobiauto.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by chensijiang on 2018/4/15 下午8:57.
 */
public final class HttpClientUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtils.class);

    private static final RequestConfig HC_REQ_CFG = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .build();

    public static ThreadLocal<HttpClient> createHttpClientThreadLocal(String httpProxyHost, Integer httpProxyPort) {
        return ThreadLocal.withInitial(() -> {
            HttpClientBuilder builder = HttpClientBuilder
                    .create()
                    .setDefaultRequestConfig(HC_REQ_CFG);
            if (httpProxyHost != null && !httpProxyHost.isEmpty()
                    && httpProxyPort != null && httpProxyPort > 0) {
                builder.setProxy(new HttpHost(httpProxyHost, httpProxyPort));
            }
            return builder
                    .build();

        });
    }

    public static ThreadLocal<HttpClient> createHttpClientThreadLocal() {
        return createHttpClientThreadLocal(
                null,
                null
        );
    }

    public static CloseableHttpResponse executeWithRetry(HttpClient hc, HttpUriRequest req, int retryTimes) {

        CloseableHttpResponse resp;
        for (int i = 0; i < retryTimes; i++) {

            try {
                resp = (CloseableHttpResponse) hc.execute(req);
                return resp;
            } catch (IOException e) {
                LOG.warn("请求失败，重试({}) error:{}, retry:{}/{}", req.getURI(), e.getMessage(), (i + 1), retryTimes);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException("Interrupted", ex);
                }

            }
        }

        throw new RuntimeException("请求多次仍然失败：" + req.getURI());
    }

    public static JSONObject parseResponse(CloseableHttpResponse response) {
        try {
            StringWriter sw = new StringWriter();
            IOUtils.copy(response.getEntity().getContent(), sw, "utf-8");
            sw.flush();
            sw.close();
            return JSON.parseObject(sw.toString());
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new RuntimeException(ex);
        }
    }

    public static void closeResp(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                LOG.warn("关闭CloseableHttpResponse时发生异常。", e);
            }
        }
    }

    private HttpClientUtils() {
        throw new RuntimeException();
    }
}
