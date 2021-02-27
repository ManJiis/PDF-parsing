package com.example.pdfdemo.controller;

import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ConcurrencyTester;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.example.pdfdemo.entry.TemplateDataModel;
import com.example.pdfdemo.service.IMerService;
import com.example.pdfdemo.util.IdUtils;
import com.example.pdfdemo.util.PdfTemplateDataProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author TANG
 * @date 2021-02-05
 */
@RestController
@RequestMapping("pdf")
public class ExportController {
    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    @Autowired
    IMerService merService;

    @GetMapping("bs/active")
    public List<String> activePdfUpload() {
        String merId = "0755-0001";
        return merService.activePdfUpload(merId);
    }

    @GetMapping("bs/entry2")
    public String entryPdfUpload2() throws Exception {
        Set<String> set = new HashSet<>();

        long startTime = System.currentTimeMillis();
        // 并发数
        int concurrency = 1000;
        CountDownLatch downLatch = new CountDownLatch(concurrency);
        CountDownLatch startingGun = new CountDownLatch(1);

        String merId = "1";
        ExecutorService executorService = new ThreadPoolExecutor(
                100, 100, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3000),
//                Executors.defaultThreadFactory(),
                new NamedThreadFactory("pdf-pool-exec-", false),
                new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i < concurrency; i++) {
            PdfTemplateDataProperties pdfTemplateDataProperties = SpringUtil.getBean(PdfTemplateDataProperties.class);
            set.add(System.identityHashCode(pdfTemplateDataProperties) + "");
            executorService.execute(() -> {
                try {
                    // 阻塞线程
                    startingGun.await();
                    merService.entryPdfUpload2(merId, pdfTemplateDataProperties);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    downLatch.countDown();
                }
            });
        }
        // 所有线程集结完毕 准备并发执行 startingGun-1
        startingGun.countDown();
        System.out.println("=========================线程开始执行==============================");
        // 执行完毕的线程阻塞再次等待其他线程
        downLatch.await();
        System.out.println("=========================线程执行结束==============================");
        long endTime = System.currentTimeMillis();
        System.out.println("\ntemplateDataProperties set size = " + set.size());
        String result = "本次任务执行时间: " + (endTime - startTime) / 1000 + "s";
        log.info(result);
        return result;
    }

    @GetMapping("bs/entry")
    public List<String> entryPdfUpload() throws Exception {
        String merId = "0755-0001";
        return merService.entryPdfUpload(merId);
    }

    @GetMapping("active")
    public List<TemplateDataModel> getActiveList() {
        PdfTemplateDataProperties pdfTemplateDataProperties = SpringUtil.getBean(PdfTemplateDataProperties.class);
        return pdfTemplateDataProperties.getBsActiveList();
    }

    @GetMapping("entry")
    public List<TemplateDataModel> getEntryList() {
        PdfTemplateDataProperties pdfTemplateDataProperties = SpringUtil.getBean(PdfTemplateDataProperties.class);
        return pdfTemplateDataProperties.getBsEntryList();
    }

    @GetMapping("test")
    public void test() {
        ExecutorService executorService = new ThreadPoolExecutor(
                3, 5, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(20),
                new NamedThreadFactory("pdf-pool-exec-", false),
                new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {
                PdfTemplateDataProperties pdfTemplateDataProperties = SpringUtil.getBean(PdfTemplateDataProperties.class);
                System.out.println("templateDataProperties = " + System.identityHashCode(pdfTemplateDataProperties));
                List<TemplateDataModel> activeList = pdfTemplateDataProperties.getBsActiveList();
                if (activeList != null && activeList.size() > 0) {
                    for (TemplateDataModel templateDataModel : activeList) {
                        System.out.println("templateDataModel = " + templateDataModel);
                    }
                }
            });
        }
    }

    @GetMapping("test2")
    public void test1() {
        ConcurrencyTester tester = ThreadUtil.concurrencyTest(10, () -> {
            merService.threadPoolTest();
        });
        // 获取总的执行时间，单位毫秒
        Console.log(tester.getInterval());
    }


    public static void main(String[] args) {
//        String template = ClassLoader.getSystemResource("templates").getPath();
//        String template = ClassLoader.getSystemResource("baosheng/pdf").getPath();
        //  /D:/code-demo/pdf-demo/target/classes/templates
//        System.out.println("templates = " + template);
//        Map<String, String> getenv = System.getenv();
//        for (String key : getenv.keySet()) {
//            System.out.println(key + " " + getenv.get(key));
//        }

        String os = System.getProperty("os.name");
        System.out.println("os = " + os);

        String os1 = System.getenv("OS");
        System.out.println("os1 = " + os1);

        Long aLong = IdUtils.snowflakeId();
        System.out.println("snowflakeId.length() = " + (aLong + "").length());

        Class<PdfTemplateDataProperties> dataPropertiesClass = PdfTemplateDataProperties.class;
        System.out.println("dataPropertiesClass.getName() = " + dataPropertiesClass.getName());

    }
}
