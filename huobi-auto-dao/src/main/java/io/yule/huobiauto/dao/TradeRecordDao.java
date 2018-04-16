package io.yule.huobiauto.dao;

import io.yule.huobiauto.entity.TradeRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chensijiang on 2018/4/15 下午8:49.
 */
public interface TradeRecordDao extends PagingAndSortingRepository<TradeRecord,String> {

    Integer countByOrderIdAndOrderState(String orderId, String orderState);
}
