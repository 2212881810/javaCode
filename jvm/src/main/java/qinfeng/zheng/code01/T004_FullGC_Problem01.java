package qinfeng.zheng.code01;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class T004_FullGC_Problem01 {

    private static class CardInfo {
        BigDecimal price = new BigDecimal(0.0);
        String name = "张三";
        int age = 5;
        Date birthdate = new Date();

        public void m() {
        }
    }

    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(50,
            new ThreadPoolExecutor.DiscardOldestPolicy());


    private static ExecutorService service = Executors.newFixedThreadPool(50);
    public static void main(String[] args) throws Exception {
        executor.setMaximumPoolSize(50);

        for (; ; ) {
            modelFit();
            Thread.sleep(100);
        }
    }

    private static void modelFit() {
        List<CardInfo> taskList = getAllCardInfo();
//        taskList.forEach(info -> {
//            ScheduledFuture<?> scheduledFuture = executor.scheduleWithFixedDelay(new Runnable() {
//                @Override
//                public void run() {
//                    info.m();
//                }
//            }, 2, 3, TimeUnit.SECONDS);
//
//
//        });


        for (CardInfo cardInfo : taskList) {
//            executor.scheduleWithFixedDelay(new Runnable() {
//                @Override
//                public void run() {
//                    cardInfo.m();
//                }
//            }, 2, 3, TimeUnit.SECONDS);
            service.submit(() -> {
                cardInfo.m();
            });
        }
    }

    private static List<CardInfo> getAllCardInfo() {
        List<CardInfo> taskList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            CardInfo ci = new CardInfo();
            taskList.add(ci);
        }

        return taskList;
    }
}