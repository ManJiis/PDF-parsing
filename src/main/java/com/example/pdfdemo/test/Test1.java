package com.example.pdfdemo.test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author TANG
 */
public class Test1 {

    public static ExecutorService executorService;

    static {
        executorService = Executors.newCachedThreadPool();
    }

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(10);

    private static final Random RANDOM = new Random();

    /**
     * 入口函数
     *
     * @param args /
     */
    public static void main(String[] args) {
        new Test1().test();
        //插入数据完成后  执行修改操作
        try {
            COUNT_DOWN_LATCH.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("它们已经插完啦..............................");
        executorService.shutdown();
    }

    public void test() {
        for (int i = 0; i < 10; i++) {
            executorService.execute(new ThreadTest());
        }
    }

    static class ThreadTest implements Runnable {

        @Override
        public void run() {
            //执行插入数据操作  每次插入一条
            // 模拟耗时
            int time = RANDOM.nextInt(10000);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "执行完了，耗时：" + time / 1000 + "秒");
            COUNT_DOWN_LATCH.countDown();
        }
    }
}