package qunar.tc.bistoury.proxy.communicate.ui.command;

import java.util.Optional;

/**
 * @author leix.xie
 * @date 2019/5/22 17:36
 * @describe
 */
public interface CommunicateCommandStore {
    Optional<CommunicateCommand> getCommunicateCommand(int code);

    Optional<CommunicateCommand> getCommunicateCommandByOldCode(int oldCode);
}
