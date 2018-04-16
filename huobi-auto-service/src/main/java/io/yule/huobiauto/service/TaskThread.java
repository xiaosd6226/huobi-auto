package io.yule.huobiauto.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.yule.huobiauto.entity.TradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

/**
 * Created by chensijiang on 2018/4/16 上午1:19.
 */
public class TaskThread implements Runnable {


    private static final Logger LOG = LoggerFactory.getLogger(TaskThread.class);

    private TaskService taskService;

    private HuobiApiService huobiApiService;

    private String taskId;

    private String taskName;

    final
    private Set<String> runningTaskIdSet;

    public TaskThread(TaskService taskService, HuobiApiService huobiApiService, String taskId, String taskName, Set<String> runningTaskIdSet) {
        this.taskService = taskService;
        this.huobiApiService = huobiApiService;
        this.taskId = taskId;
        this.taskName = taskName;
        this.runningTaskIdSet = runningTaskIdSet;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(this.taskName);
        LOG.info("任务已启动。");
        synchronized (this.runningTaskIdSet) {
            this.runningTaskIdSet.add(this.taskId);
        }
        try {

            TradeTask task = this.taskService.getTask(this.taskId);
            if (task == null) {
                LOG.info("任务未找到：{}", this.taskId);
                return;
            }
            String spotAccountId = null;
            JSONObject joAccounts = this.huobiApiService.getAccounts();
            JSONArray joAccountsDataArray = joAccounts.getJSONArray("data");
            if (joAccountsDataArray != null && !joAccounts.isEmpty()) {
                for (Object o : joAccountsDataArray) {
                    JSONObject joAccountsDataItem = (JSONObject) o;
                    if (joAccountsDataItem.getString("type").equals("spot") &&
                            joAccountsDataItem.getString("state").equals("working")) {
                        spotAccountId = joAccountsDataItem.getString("id");
                        break;
                    }
                }
            }
            if (spotAccountId == null) {
                LOG.warn("未获取到accountId。");
                return;
            } else {
                LOG.info("取得accountId：{}", spotAccountId);
            }

            JSONObject joTradeDetail = this.huobiApiService.getTradeDetail(task.getSymbol());
            JSONArray ja = joTradeDetail.getJSONObject("tick").getJSONArray("data");
            JSONObject oneData = ja.getJSONObject(0);
            BigDecimal currentPrice = oneData.getBigDecimal("price");
            Timestamp time = new Timestamp(oneData.getLong("ts"));
            LOG.info("{}当前价格：{} 时间：{}", task.getSymbol(), currentPrice, time);

            String currentOrderId = task.getCurrentOrderId();
            if (currentOrderId != null && !currentOrderId.isEmpty()) {
                JSONObject joOrderDetail = this.huobiApiService.getOrderDetail(currentOrderId);
                JSONObject orderDetailData = joOrderDetail.getJSONObject("data");
                if (!joOrderDetail.getString("status").equals("ok") || orderDetailData == null || orderDetailData.isEmpty()) {
                    LOG.warn("当前订单不存在：{}", currentOrderId);
                    return;
                }
//                pre-submitted 准备提交, submitting , submitted 已提交, partial-filled 部分成交, partial-canceled 部分成交撤销, filled 完全成交, canceled 已撤销
                String orderState = orderDetailData.getString("state");
                boolean buy = orderDetailData.getString("type").equals("buy-limit");
                LOG.info("当前订单（{}）状态：{} 类型：{}", currentOrderId, orderState, buy ? "买入" : "卖出");
                if (!orderState.equals("submitted") && !orderState.equals("filled")) {
                    LOG.warn("无法处理的订单状态。");
                    return;
                }

                if (orderState.equals("submitted")) {
                    //已提交，等待成交。
                    LOG.info("已提交的订单，等待成交。");
                } else if (orderState.equals("filled")) {
                    //已成交。
                    if(buy){
                        //如果是买入成交，那么要等待价格满足条件时，创建卖出委托。
                    }else {
                        //如果是卖出成交，那么要等待价格满足条件时，创建买入委托。
                    }
                }
            } else {

                JSONObject joCreateOrder = this.huobiApiService.createDelegateOrder(
                        spotAccountId,
                        task.getSymbol(),
                        task.getTradeCount(),
                        currentPrice,
                        true
                );
                String orderId = joCreateOrder.getString("data");
                if (orderId == null || orderId.isEmpty()) {
                    LOG.warn("创建买入委托失败。");
                    return;
                }
                LOG.info("创建买入委托成功，订单号：{}");
                this.taskService.updateCurrentOrderId(this.taskId, orderId);
            }


            this.huobiApiService.getCurrentDelegates(task.getSymbol());
        } finally {
            synchronized (this.runningTaskIdSet) {
                this.runningTaskIdSet.remove(this.taskId);
                LOG.info("任务移除：{}", this.taskId);
            }
        }

    }
}
