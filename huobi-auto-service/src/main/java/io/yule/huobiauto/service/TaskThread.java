package io.yule.huobiauto.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.yule.huobiauto.entity.TradeRecord;
import io.yule.huobiauto.entity.TradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

import static io.yule.huobiauto.entity.EnumerationConstants.*;

/**
 * Created by chensijiang on 2018/4/16 上午1:19.
 */
public class TaskThread implements Runnable {


    private static final Logger LOG = LoggerFactory.getLogger(TaskThread.class);

    private TaskService taskService;

    private HuobiApiService huobiApiService;

    private EmailNotifyService emailNotifyService;

    private String taskId;

    private String taskName;

    final
    private Set<String> runningTaskIdSet;

    public TaskThread(TaskService taskService, HuobiApiService huobiApiService, EmailNotifyService emailNotifyService, String taskId, String taskName, Set<String> runningTaskIdSet) {
        this.taskService = taskService;
        this.huobiApiService = huobiApiService;
        this.emailNotifyService = emailNotifyService;
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

            for (; ; ) {
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

                JSONObject joBalance = this.huobiApiService.getAccountBalance(spotAccountId);
                JSONArray balanceList = joBalance.getJSONObject("data").getJSONArray("list");
                BigDecimal currBalance = null;
                BigDecimal frozenBalance = null;
                for (Object o : balanceList) {
                    if (currBalance != null && frozenBalance != null) {
                        break;
                    }
                    JSONObject bjo = (JSONObject) o;
                    if (bjo.getString("currency").equals("usdt")) {
                        if (bjo.getString("type").equals("trade")) {
                            currBalance = bjo.getBigDecimal("balance");
                        } else {
                            frozenBalance = bjo.getBigDecimal("balance");
                        }
                    }
                }

                LOG.info("交易余额：{} 冻结余额：{}", currBalance != null ? currBalance.toPlainString() : "0", frozenBalance != null ? frozenBalance.toPlainString() : "0");
                this.taskService.updateBalance(taskId, currBalance, frozenBalance);

                JSONObject joTradeDetail = this.huobiApiService.getTradeDetail(task.getSymbol());
                JSONArray ja = joTradeDetail.getJSONObject("tick").getJSONArray("data");
                JSONObject oneData = ja.getJSONObject(0);
                BigDecimal currentPrice = oneData.getBigDecimal("price");
                Timestamp time = new Timestamp(oneData.getLong("ts"));
                LOG.info("{}当前价格：{} 时间：{}", task.getSymbol(), currentPrice, time);


                this.taskService.createTradeTickLog(
                        this.taskId,
                        currentPrice,
                        time
                );

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
                    String orderType = orderDetailData.getString("type");
                    boolean buy = orderType.equals(buyLimit);
                    BigDecimal tradePrice = orderDetailData.getBigDecimal("price");
                    BigDecimal tradeCount = orderDetailData.getBigDecimal("amount");
                    Timestamp createdAt = orderDetailData.getTimestamp("created-at");
                    Long aLong = orderDetailData.getLong("finished-at");
                    Timestamp finishedAt = aLong == null || aLong == 0 ? null : new Timestamp(aLong);
                    LOG.info("当前订单（{}）状态：{} 类型：{} 成交价：{} 成交数量：{}",
                            currentOrderId, orderState, buy ? "买入" : "卖出", tradePrice, tradeCount);
                    if (!orderState.equals(submitted) && !orderState.equals(filled)) {
                        LOG.warn("无法处理的订单状态。");
                        return;
                    }
                    if (this.taskService.createTradeRecord(
                            this.taskId,
                            currentOrderId,
                            orderState,
                            orderType,
                            tradePrice,
                            tradeCount,
                            finishedAt,
                            createdAt
                    ) && orderState.equals(submitted)) {
                        this.emailNotifyService.send(
                                task.getNotifyEmail(),
                                "委托成交",
                                String.format("当前订单（%s）状态：%s 类型：%s 成交价：%s 成交数量：%s",
                                        currentOrderId, orderState, buy ? "买入" : "卖出", tradePrice, tradeCount)
                        );
                    }

                    if (orderState.equals(submitted)) {
                        //已提交，等待成交。
                        LOG.info("已提交的订单，等待成交。");
                    } else if (orderState.equals(filled)) {
                        //已成交。
                        if (buy) {
                            //如果是买入成交，那么要等待价格满足条件时，创建卖出委托。
                            BigDecimal expectPrice = tradePrice.multiply(
                                    task.getPriceChangeThresholdPercent().divide(BigDecimal.valueOf(100), BigDecimal.ROUND_CEILING)
                            ).add(tradePrice);

                            this.createOrder(
                                    spotAccountId,
                                    task,
                                    expectPrice,
                                    false
                            );
                        } else {
                            //如果是卖出成交，那么要等待价格满足条件时，创建买入委托。
                            TradeRecord lastFinishedTradeRecord = this.taskService.findLastFinishedTradeRecord(this.taskId);
                            BigDecimal expectPrice;
                            if (lastFinishedTradeRecord == null) {
                                expectPrice = currentPrice;
                            } else {
                                BigDecimal lastSellPrice = lastFinishedTradeRecord.getDelegateAmount();
                                expectPrice =
                                        lastSellPrice.subtract(lastSellPrice.multiply(
                                                task.getPriceChangeThresholdPercent().divide(BigDecimal.valueOf(100), BigDecimal.ROUND_CEILING)
                                        ));
                            }

                            this.createOrder(
                                    spotAccountId,
                                    task,
                                    expectPrice,
                                    true
                            );
                        }
                    } else {
                        throw new RuntimeException("未知状态：" + orderState);
                    }
                } else {

                    this.createOrder(
                            spotAccountId,
                            task,
                            currentPrice,
                            true
                    );

                }

                try {
                    LOG.info("任务休眠：{}秒。", task.getTickSeconds());
                    Thread.sleep(task.getTickSeconds() * 1000);
                    LOG.info("任务休眠结束。");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            synchronized (this.runningTaskIdSet) {
                this.runningTaskIdSet.remove(this.taskId);
                LOG.info("任务移除：{}", this.taskId);
            }
        }

    }

    private boolean createOrder(String spotAccountId, TradeTask task, BigDecimal price, boolean buy) {
        //卖出要考虑手续费0.2%
        BigDecimal cnt = task.getTradeCount();
        if (!buy) {
            cnt = cnt.subtract(cnt.multiply(BigDecimal.valueOf(0.2 / 100)));
        }

        JSONObject joCreateOrder = this.huobiApiService.createDelegateOrder(
                spotAccountId,
                task.getSymbol(),
                cnt,
                price,
                buy
        );
        String orderId = joCreateOrder.getString("data");
        if (orderId == null || orderId.isEmpty()) {
            LOG.warn("创建{}委托失败。", buy ? "买入" : "卖出");
            return false;
        }
        LOG.info("创建{}委托成功，订单号：{} 委托价格：{}", buy ? "买入" : "卖出", orderId, price);
        this.taskService.updateCurrentOrderId(this.taskId, orderId);

        this.emailNotifyService.send(
                task.getNotifyEmail(),
                "创建委托成功",
                String.format("创建%s委托成功，订单号：%s 委托价格：%s", buy ? "买入" : "卖出", orderId, price)
        );

        return true;
    }
}
