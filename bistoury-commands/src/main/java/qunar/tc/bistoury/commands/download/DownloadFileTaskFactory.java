package qunar.tc.bistoury.commands.download;

import com.google.common.collect.ImmutableSet;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.command.DownloadCommand;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;

/**
 * @author leix.xie
 * @date 2019/11/5 15:43
 * @describe
 */
public class DownloadFileTaskFactory implements TaskFactory<DownloadCommand> {
    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_DOWNLOAD_FILE.getCode());
    }

    @Override
    public String name() {
        return "download file";
    }

    @Override
    public Task create(RemotingHeader header, DownloadCommand command, ResponseHandler handler) {
        return new DownloadFileTask(header.getId(), header.getMaxRunningMs(), command, handler);
    }
}
