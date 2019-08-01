package qunar.tc.bistoury.instrument.client.monitor;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

/**
 * @author: leix.xie
 * @date: 2018/12/28 14:16
 * @describe：
 */
public interface Monitor {
    boolean startup(InstrumentInfo instrumentInfo);

    String addMonitor(final String source, final int line);

    void removeMonitor(final String source, final int line, String monitorId);

    void destroy();
}
