package qunar.tc.bistory;

import qunar.tc.bistoury.instrument.client.metrics.Metrics;
import qunar.tc.bistoury.instrument.client.metrics.MetricsReportor;
import qunar.tc.bistoury.instrument.client.metrics.QMonitorMetricsReportor;

import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;

/**
 * @author: leix.xie
 * @date: 2018/12/28 15:37
 * @describe：
 */
public class QmonitorTest {


    static final MetricsReportor reportor = new QMonitorMetricsReportor(Metrics.INSTANCE);
    static final PrintWriter writer = new PrintWriter(System.out);

    public static void premain(String agentArgs, Instrumentation inst) {
    }

    public static void main(String[] args) {
        startReport();
    }

    public static void startReport() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    reportor.report("");
                    writer.println("------------------");
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
