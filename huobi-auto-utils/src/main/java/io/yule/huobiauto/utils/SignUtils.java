package io.yule.huobiauto.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chensijiang on 2018/4/15 下午9:14.
 */
public final class SignUtils {


    public static Timestamp utcTimestamp() {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        return timestamp;
    }


    public static String formatTs(Timestamp ts) {
        String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts).replace(" ", "T");
        return strDate;
    }

    public static String utcTimestampStr() {
        return formatTs(utcTimestamp());
    }

    public static String createSign(String method,
                                    String host,
                                    String url,
                                    String accessKey,
                                    String secretKey,
                                    Timestamp ts,
                                    Map<String, String> otherParams
    ) {
        StringBuilder signContent = new StringBuilder();
        signContent.append(method).append("\n");
        signContent.append(host).append("\n");
        signContent.append(url).append("\n");

        String strTs = formatTs(ts);
        Map<String, String> params = new HashMap<>();
        params.put("AccessKeyId", encode(accessKey));
        params.put("SignatureMethod", "HmacSHA256");
        params.put("SignatureVersion", "2");
        params.put("Timestamp", encode(strTs));
        if (otherParams != null && !otherParams.isEmpty()) {
            otherParams.forEach((k, v) -> {
                params.put(k, encode(v));
            });
        }
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder psort = new StringBuilder();
        for (String key : keys) {
            psort.append(key).append("=").append(params.get(key)).append("&");
        }
        psort.deleteCharAt(psort.length() - 1);


        signContent.append(psort);

        String message = signContent.toString();
        try {

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
            return hash;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public static String encode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private SignUtils() {
        throw new RuntimeException();
    }
}
