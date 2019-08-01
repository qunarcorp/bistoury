package qunar.tc.bistoury.instrument.client.monitor;

import java.lang.annotation.*;

/**
 * Created by zhaohui.yu
 * 5/25/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AgentGenerated {
}
