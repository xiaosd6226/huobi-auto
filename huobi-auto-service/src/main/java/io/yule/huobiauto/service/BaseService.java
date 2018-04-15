package io.yule.huobiauto.service;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by chensijiang on 2018/4/15 下午9:13.
 */
public abstract class BaseService {
    protected String createId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    protected Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
}
