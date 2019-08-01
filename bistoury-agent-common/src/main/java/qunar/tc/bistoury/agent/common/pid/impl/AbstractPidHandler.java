package qunar.tc.bistoury.agent.common.pid.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.pid.PidHandler;

/**
 * @author zhenyu.nie created on 2019 2019/4/3 14:29
 */
public abstract class AbstractPidHandler implements PidHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPidHandler.class);

    @Override
    public final int getPid() {
        try {
            return doGetPid();
        } catch (Exception e) {
            logger.error("get pid error, {}", getClass().getName(), e);
            return -1;
        }
    }

    protected abstract int doGetPid();
}
