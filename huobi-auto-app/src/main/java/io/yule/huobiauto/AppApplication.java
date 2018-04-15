package io.yule.huobiauto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by chensijiang on 2018/4/14 下午12:50.
 */
@SpringBootApplication
@EnableScheduling
public class AppApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(AppApplication.class, args);
    }
}
