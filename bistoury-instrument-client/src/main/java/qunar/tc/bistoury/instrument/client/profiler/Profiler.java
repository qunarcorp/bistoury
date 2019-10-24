package qunar.tc.bistoury.instrument.client.profiler;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

/**
 * @author cai.wen created on 2019/10/23 10:18
 */
public interface Profiler {

    void startup(InstrumentInfo instrumentInfo);

    void destroy();
}
