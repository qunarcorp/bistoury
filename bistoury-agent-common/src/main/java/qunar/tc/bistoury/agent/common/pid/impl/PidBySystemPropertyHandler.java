package qunar.tc.bistoury.agent.common.pid.impl;

import qunar.tc.bistoury.agent.common.pid.PidHandler;

/**
 * @author leix.xie
 * @date 2019/7/11 20:19
 * @describe
 */
public class PidBySystemPropertyHandler extends AbstractPidHandler implements PidHandler {

    @Override
    public int priority() {
        return Priority.FROM_SYSTEM_PROPERTY_PRIORITY;
    }

    @Override
    protected int doGetPid() {
        return Integer.valueOf(System.getProperty("bistoury.user.pid", "-1"));
    }

}
