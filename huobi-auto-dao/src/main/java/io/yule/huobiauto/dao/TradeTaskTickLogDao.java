package io.yule.huobiauto.dao;

import io.yule.huobiauto.entity.TradeTaskTickLog;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chensijiang on 2018/4/15 下午8:50.
 */
public interface TradeTaskTickLogDao extends PagingAndSortingRepository<TradeTaskTickLog, String> {
}
