package qunar.tc.bistoury.commands.download;

import com.google.common.collect.ImmutableSet;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;

/**
 * @author leix.xie
 * @date 2019/11/4 16:23
 * @describe
 */
public class DownloadFileListTaskFactory implements TaskFactory<String> {
    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_LIST_DOWNLOAD_FILE.getCode());
    }

    @Override
    public String name() {
        return "list download file";
    }

    @Override
    public Task create(RemotingHeader header, String command, ResponseHandler handler) {
        return new DownloadFileListTask(header.getId(), command, handler, header.getMaxRunningMs());
    }
}
