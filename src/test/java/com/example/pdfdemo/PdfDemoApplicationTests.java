package com.example.pdfdemo;

import com.example.pdfdemo.config.ExecutorConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PdfDemoApplication.class)
class PdfDemoApplicationTests {

    @Autowired
    ExecutorConfig executorConfig;

    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(10);

    @Test
    void contextLoads() {
/*        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            try {
                latch.await();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                latch.countDown();
            }
        }*/
    }

}
