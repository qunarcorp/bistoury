package qunar.tc.bistoury.instrument.client.profiler;

/**
 * @author cai.wen created on 2019/10/23 10:18
 */
public interface Profiler {

    String getId();

    String getStatus();

    void start();

    void stop();
}
