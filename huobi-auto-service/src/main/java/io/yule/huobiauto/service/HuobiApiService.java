package io.yule.huobiauto.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.yule.huobiauto.utils.HttpClientUtils;
import io.yule.huobiauto.utils.SignUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * Created by chensijiang on 2018/4/15 下午4:55.
 */
@Component
public class HuobiApiService {

    private static final Logger LOG = LoggerFactory.getLogger(HuobiApiService.class);

    @Value("${huobi.accessKey}")
    private String accessKey;

    @Value("${huobi.secretKey}")
    private String secretKey;

    private static final String API_HOST = "api.huobipro.com";

    private static final String API_URI_PREFIX = "https://" + API_HOST;

    private ThreadLocal<HttpClient> hcThreadLocal = HttpClientUtils.createHttpClientThreadLocal();


    public JSONObject getTradeDetail(String symbol) {
        LOG.info("获取最新成交价：{}", symbol);
        HttpClient hc = hcThreadLocal.get();
        HttpUriRequest req = RequestBuilder
                .get(API_URI_PREFIX + "/market/trade")
                .addParameter("symbol", symbol)
                .build();
        CloseableHttpResponse resp = null;
        try {
            resp = HttpClientUtils.executeWithRetry(hc, req, 3);
            JSONObject jo = HttpClientUtils.parseResponse(resp);
            LOG.info("最新成交价返回：\n{}", JSON.toJSONString(jo, true));
            return jo;
        } finally {
            HttpClientUtils.closeResp(resp);
        }

    }

    public JSONObject getKLine(String symbol) {
        LOG.info("获取行情：{}", symbol);
        HttpClient hc = hcThreadLocal.get();
        HttpUriRequest req = RequestBuilder
                .get(API_URI_PREFIX + "/market/history/kline")
                .addParameter("symbol", symbol)
                .addParameter("period", "1min")
                .addParameter("size", "1")
                .build();
        CloseableHttpResponse resp = null;
        try {
            resp = HttpClientUtils.executeWithRetry(hc, req, 3);
            JSONObject jo = HttpClientUtils.parseResponse(resp);
            LOG.info("行情返回：\n{}", JSON.toJSONString(jo, true));
            return jo;
        } finally {
            HttpClientUtils.closeResp(resp);
        }


    }


    public JSONObject getAccounts() {
        LOG.info("获取账户列表。");
        HttpClient hc = hcThreadLocal.get();
        Timestamp ts = SignUtils.utcTimestamp();
        String sign = SignUtils.createSign(
                "GET",
                API_HOST,
                "/v1/account/accounts",
                this.accessKey,
                this.secretKey,
                ts,
                null
        );
        HttpUriRequest req = RequestBuilder
                .get(API_URI_PREFIX + "/v1/account/accounts")
                .addParameter("AccessKeyId", this.accessKey)
                .addParameter("SignatureMethod", "HmacSHA256")
                .addParameter("SignatureVersion", "2")
                .addParameter("Timestamp", SignUtils.formatTs(ts))
                .addParameter("Signature", sign)
                .build();
        CloseableHttpResponse resp = null;
        try {
            resp = HttpClientUtils.executeWithRetry(hc, req, 3);
            JSONObject jo = HttpClientUtils.parseResponse(resp);
            LOG.info("账户列表返回：\n{}", JSON.toJSONString(jo, true));
            return jo;
        } finally {
            HttpClientUtils.closeResp(resp);
        }
    }


    public JSONObject getAccountBalance(String id, String type) {
        LOG.info("获取账户余额：{} {}", id, type);
        HttpClient hc = hcThreadLocal.get();
        String url = "/v1/account/accounts/" + id + "/balance";
        Timestamp ts = SignUtils.utcTimestamp();

        String sign = SignUtils.createSign(
                "GET",
                API_HOST,
                url,
                this.accessKey,
                this.secretKey,
                ts,
                null
        );
        HttpUriRequest req = RequestBuilder
                .get(API_URI_PREFIX + url)
                .addParameter("AccessKeyId", this.accessKey)
                .addParameter("SignatureMethod", "HmacSHA256")
                .addParameter("SignatureVersion", "2")
                .addParameter("Timestamp", SignUtils.formatTs(ts))
                .addParameter("Signature", sign)
                .build();
        CloseableHttpResponse resp = null;
        try {
            resp = HttpClientUtils.executeWithRetry(hc, req, 3);
            JSONObject jo = HttpClientUtils.parseResponse(resp);
            LOG.info("账户余额返回：\n{}", JSON.toJSONString(jo, true));
            return jo;
        } finally {
            HttpClientUtils.closeResp(resp);
        }
    }
}
