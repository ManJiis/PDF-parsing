package com.example.pdfdemo.schedulerjob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 测试spring定时任务执行
 *
 * @author TANG
 */
@Component
@EnableScheduling
public class MyTestTask {
    private final static Logger log = LoggerFactory.getLogger(MyTestTask.class);

    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedDelay = 60000)
    public void executeUpdateYqTask() {
        log.info(Thread.currentThread().getName() + " >>> task [one] " + SIMPLE_DATE_FORMAT.format(new Date()));
    }

    @Scheduled(fixedDelay = 60000)
    public void executeRepaymentTask() throws InterruptedException {
        log.info(Thread.currentThread().getName() + " >>> task [two] " + SIMPLE_DATE_FORMAT.format(new Date()));
//        Thread.sleep(5000);
    }
    @Scheduled(fixedDelay = 60000)
    public void executeRepayment2Task() throws InterruptedException {
        log.info(Thread.currentThread().getName() + " >>> task [two-2] " + SIMPLE_DATE_FORMAT.format(new Date()));
//        Thread.sleep(5000);
    }
}