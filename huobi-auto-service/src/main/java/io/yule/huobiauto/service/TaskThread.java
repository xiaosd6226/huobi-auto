package io.yule.huobiauto.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by chensijiang on 2018/4/16 上午1:19.
 */
public class TaskThread implements Runnable {


    private static final Logger LOG = LoggerFactory.getLogger(TaskThread.class);

    private TaskService taskService;

    private String taskId;

    private String taskName;

    private Set<String> runningTaskIdSet;

    public TaskThread(TaskService taskService, String taskId, String taskName, Set<String> runningTaskIdSet) {
        this.taskService = taskService;
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
    }
}
