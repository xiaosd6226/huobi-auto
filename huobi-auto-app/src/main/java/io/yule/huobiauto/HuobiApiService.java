package io.yule.huobiauto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chensijiang on 2018/4/15 下午4:55.
 */
@Component
public class HuobiApiService {

    @Value("${huobi.accessKey}")
    private String accessKey;

    @Value("${huobi.secretKey}")
    private String secretKey;
}
