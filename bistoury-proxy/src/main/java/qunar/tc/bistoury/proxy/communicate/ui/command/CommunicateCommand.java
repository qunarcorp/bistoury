package qunar.tc.bistoury.proxy.communicate.ui.command;

import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.CommunicateCommandProcessor;

/**
 * @author zhenyu.nie created on 2019 2019/5/16 16:23
 */
public class CommunicateCommand {

    private final int code;

    private final int minAgentVersion;

    private final boolean supportMulti;

    private final CommunicateCommandProcessor<?> processor;

    public CommunicateCommand(int code, int minAgentVersion, boolean supportMulti, CommunicateCommandProcessor processor) {
        this.code = code;
        this.minAgentVersion = minAgentVersion;
        this.supportMulti = supportMulti;
        this.processor = processor;
    }

    public int getCode() {
        return code;
    }

    public int getMinAgentVersion() {
        return minAgentVersion;
    }

    public boolean isSupportMulti() {
        return supportMulti;
    }

    public CommunicateCommandProcessor<?> getProcessor() {
        return processor;
    }
}
