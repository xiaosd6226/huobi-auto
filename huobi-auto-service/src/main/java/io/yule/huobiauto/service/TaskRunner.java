package io.yule.huobiauto.service;

import io.yule.huobiauto.entity.TradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chensijiang on 2018/4/16 上午1:07.
 */
@Component
public class TaskRunner extends BaseService {
    private static final Logger LOG = LoggerFactory.getLogger(TaskRunner.class);

    @Resource
    private TaskService taskService;

    @Resource
    private HuobiApiService huobiApiService;

    @Resource
    private EmailNotifyService emailNotifyService;

    private ExecutorService taskThreadPool =
            Executors.newFixedThreadPool(10);

    private Set<String> runningTaskIdSet = new HashSet<>();

    public static void main(String[] args) throws Exception {
       double a = 200.0;
       double b = 0.2;
        System.out.println(a*b/100); //0.2


    }

    @Scheduled(fixedRate = 60 * 1000L)
    public void fetchAndRunTask() {

        LOG.info("开始加载任务。");

        List<TradeTask> taskList = this.taskService.findAllTasks();

        if (taskList.isEmpty()) {
            LOG.info("未读取到任何任务。");
            return;
        }

        LOG.info("加载到的任务数量：{}", taskList.size());

        for (TradeTask tradeTask : taskList) {

            synchronized (this.runningTaskIdSet) {
                if (this.runningTaskIdSet.contains(tradeTask.getId())) {
                    LOG.info("任务（{}）已经在运行中，跳过。", tradeTask.getId());
                } else {
                    LOG.info("任务（{}）准备启动。", tradeTask.getId());
                    this.taskThreadPool.execute(new TaskThread(
                            this.taskService,
                            this.huobiApiService,
                            this.emailNotifyService,
                            tradeTask.getId(),
                            tradeTask.getTaskName(),
                            this.runningTaskIdSet
                    ));

                }
            }

        }

    }

}
