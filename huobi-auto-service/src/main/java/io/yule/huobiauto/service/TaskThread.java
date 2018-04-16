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

            JSONObject joAccounts = this.huobiApiService.getAccounts();
            JSONObject joBalance = this.huobiApiService.getAccountBalance("2950507", "spot");


            JSONObject kLineJSON = this.huobiApiService.getTradeDetail(task.getSymbol());
            JSONArray ja = kLineJSON.getJSONObject("tick").getJSONArray("data");
            JSONObject oneData = ja.getJSONObject(0);
            BigDecimal closePrice = oneData.getBigDecimal("price");
            Timestamp time = new Timestamp(oneData.getLong("ts"));
            LOG.info("成交价：{} 时间：{}", closePrice, time);
        } finally {
            synchronized (this.runningTaskIdSet) {
                this.runningTaskIdSet.remove(this.taskId);
                LOG.info("任务移除：{}", this.taskId);
            }
        }

    }
}
