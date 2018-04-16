package io.yule.huobiauto.service;

import io.yule.huobiauto.dao.TradeRecordDao;
import io.yule.huobiauto.dao.TradeTaskDao;
import io.yule.huobiauto.dao.TradeTaskTickLogDao;
import io.yule.huobiauto.entity.EnumerationConstants;
import io.yule.huobiauto.entity.TradeRecord;
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

    @Resource
    private TradeRecordDao tradeRecordDao;

    @Transactional(Transactional.TxType.SUPPORTS)
    public TradeTask getTask(String id) {
        return this.tradeTaskDao.findOne(id);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TradeTask> findAllTasks() {
        Iterable<TradeTask> list = this.tradeTaskDao.findAll();
        if (list == null) {
            return Collections.emptyList();
        }
        return (List<TradeTask>) list;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createTradeTickLog(
            String taskId,
            BigDecimal price,
            Timestamp priceTime
    ) {
        TradeTaskTickLog tt = new TradeTaskTickLog();
        tt.setId(createId());
        tt.setCreatedDate(now());
        tt.setCurrentPrice(price);
        tt.setPriceTime(priceTime);
        tt.setTaskId(taskId);
        this.tradeTaskTickLogDao.save(tt);
    }


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void updateCurrentOrderId(String taskId, String orderId) {
        TradeTask task = this.tradeTaskDao.findOne(taskId);
        task.setCurrentOrderId(orderId);
        this.tradeTaskDao.save(task);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void updateBalance(String taskId, BigDecimal balance, BigDecimal frozenBalance) {
        TradeTask task = this.tradeTaskDao.findOne(taskId);
        task.setCurrentBalance(balance);
        task.setFrozenBalance(frozenBalance);
        this.tradeTaskDao.save(task);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean createTradeRecord(String taskId,
                                     String orderId,
                                     String orderState,
                                     String orderType,
                                     BigDecimal price,
                                     BigDecimal count,
                                     Timestamp finishedTs,
                                     Timestamp createdTs
    ) {

        if (this.tradeRecordDao.countByOrderIdAndOrderState(orderId, orderState) > 0) {
            return false;
        }

        TradeRecord tr = new TradeRecord();
        tr.setId(super.createId());
        tr.setCreatedDate(now());
        tr.setTaskId(taskId);
        tr.setOrderId(orderId);
        tr.setDealAmount(count);
        tr.setDelegateAmount(price);
        tr.setDelegateCreatedTime(createdTs);
        tr.setDelegateFinishedTime(finishedTs);
        tr.setOrderState(orderState);
        tr.setOrderType(orderType);
        this.tradeRecordDao.save(tr);

        return true;
    }


    public TradeRecord findLastFinishedTradeRecord(String taskId) {
        return this.tradeRecordDao.findFirstByTaskIdAndOrderTypeAndOrderStateOrderByDelegateFinishedTimeDesc(
                taskId,
                EnumerationConstants.sellLimit,
                EnumerationConstants.filled
        );
    }
}
