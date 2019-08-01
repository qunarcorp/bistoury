package qunar.tc.bistoury.instrument.client.monitor;

import java.security.ProtectionDomain;

/**
 * Created by zhaohui.yu
 * 15/10/19
 */
public interface Matcher {
    boolean match(ProtectionDomain domain);
}
