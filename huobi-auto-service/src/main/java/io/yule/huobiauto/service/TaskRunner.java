package io.yule.huobiauto.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.yule.huobiauto.entity.TradeTask;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
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

    private ExecutorService taskThreadPool =
            Executors.newFixedThreadPool(10);

    private Set<String> runningTaskIdSet = new HashSet<>();

    public static void main(String[] args) throws Exception {
        BigDecimal bigDecimal = BigDecimal.valueOf(1.1239123000000);

        System.out.println(bigDecimal.toPlainString());
//        System.out.println(new Timestamp(1523846380248L));
//        double a=1.8175;
//        double b=1.8073;
//        System.out.println((a - b) / a*100);
//
//        Email email = new SimpleEmail();
//        email.setHostName("smtp.163.com");
//        email.setSmtpPort(465);
//        email.setAuthenticator(new DefaultAuthenticator("18666293535@163.com", "chensj1987"));
//        email.setSSLOnConnect(true);
//        email.setFrom("18666293535@163.com");
//        email.setSubject("您好TestMail");
//        email.setMsg("换货This is a test mail ... :-)");
//        email.addTo("121330950@qq.com");
//        email.send();

        String json="{\"objs\":[\n" +
                "\t\t{\n" +
                "\t\t    \"addr\":\"28:c6:8e:39:03:c7\",\n" +
                "\t\t    \"ssid\":\"BAIYI0001\",\n" +
                "\t\t    \"rssi\":-50\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t    \"addr\":\"54:35:30:0c:68:02\",\n" +
                "\t\t    \"ssid\":\"HP-HOTSPOT-02-LaserJet M1218\",\n" +
                "\t\t    \"rssi\":-52\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t    \"addr\":\"5c:0e:8b:8d:bf:31\",\n" +
                "\t\t    \"ssid\":\"CMCC-FREE\",\n" +
                "\t\t    \"rssi\":-80\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t    \"addr\":\"5c:0e:8b:8d:bf:30\",\n" +
                "\t\t    \"ssid\":\"CMCC-WEB\",\n" +
                "\t\t    \"rssi\":-82\n" +
                "\t\t} ]}";
        JSONObject root = JSON.parseObject(json);
        JSONArray arr = root.getJSONArray("objs");
        for (Object o : arr) {
            JSONObject jo = (JSONObject) o;
            System.out.println(jo.getString("addr"));
            System.out.println(jo.getString("ssid"));
        }

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
                            tradeTask.getId(),
                            tradeTask.getTaskName(),
                            this.runningTaskIdSet
                    ));

                }
            }

        }

    }

}
