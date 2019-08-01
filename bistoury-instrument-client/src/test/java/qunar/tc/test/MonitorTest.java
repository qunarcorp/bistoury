package qunar.tc.test;

import qunar.tc.bistoury.instrument.client.metrics.Metrics;
import qunar.tc.bistoury.instrument.client.metrics.MetricsReportor;
import qunar.tc.bistoury.instrument.client.metrics.QMonitorMetricsReportor;
import qunar.tc.bistoury.instrument.client.metrics.Timer;

import java.util.Random;

/**
 * @author: leix.xie
 * @date: 2018/12/27 15:03
 * @describe：
 */
public class MonitorTest {

    static final MetricsReportor reportor = new QMonitorMetricsReportor(Metrics.INSTANCE);
    private static final Random random = new Random();
    /**
     * 主方法
     */
    public static void main(String[] args) throws Exception {
        Timer.Context aTimer = Metrics.timer("aTimer").get().time();
        startReport();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            test();
        }
        Metrics.timer("timer").get().time(start).directGet();
        aTimer.stop();
    }

    public static void test() throws Exception {
        Metrics.counter("count").delta().get().inc();
        Timer.Context bTimer = Metrics.timer("bTimer").get().time();
        //Thread.sleep(random.nextInt(1000));
        bTimer.stop();
    }

    public static void startReport() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    reportor.report("");
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}

