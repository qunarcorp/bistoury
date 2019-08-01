package qunar.tc.bistoury.proxy.communicate.ui.command;

import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.CommunicateCommandProcessor;

import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/22 14:31
 */
public interface UiRequestCommand {

    Set<Integer> getCodes();

    int getMinAgentVersion();

    boolean supportMulti();

    CommunicateCommandProcessor getProcessor();
}
