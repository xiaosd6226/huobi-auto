package io.yule.huobiauto.service;

import io.yule.huobiauto.dao.TradeTaskDao;
import io.yule.huobiauto.dao.TradeTaskTickLogDao;
import io.yule.huobiauto.entity.TradeTask;
import io.yule.huobiauto.entity.TradeTaskTickLog;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * Created by chensijiang on 2018/4/15 下午9:13.
 */
@Component
public class TaskService extends BaseService {

    @Resource
    private TradeTaskDao tradeTaskDao;

    @Resource
    private TradeTaskTickLogDao tradeTaskTickLogDao;

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TradeTask> findAllTasks() {
        Iterable<TradeTask> list = this.tradeTaskDao.findAll();
        if (list == null) {
            return Collections.emptyList();
        }
        return (List<TradeTask>) list;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void createTradeTickLog(
            String taskId,
            BigDecimal price,
            Timestamp priceTime,
            BigDecimal expectPrice,
            String logType
    ) {
        TradeTaskTickLog tt = new TradeTaskTickLog();
        tt.setId(createId());
        tt.setCreatedDate(now());
        tt.setCurrentPrice(price);
        tt.setPriceTime(priceTime);
        tt.setExpectPrice(expectPrice);
        tt.setLogType(logType);
        tt.setTaskId(taskId);
        this.tradeTaskTickLogDao.save(tt);
    }

}
