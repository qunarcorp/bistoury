package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.proxy.util.DownloadDirUtils;
import qunar.tc.bistoury.remoting.command.DownloadCommand;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RequestData;

import java.util.Optional;
import java.util.Set;

/**
 * @author leix.xie
 * @date 2019/11/5 15:30
 * @describe
 */
@Service
public class DownloadFileProcessor extends AbstractCommand<DownloadCommand> {
    private static final Logger logger = LoggerFactory.getLogger(DownloadFileProcessor.class);

    @Override
    protected Optional<RequestData<DownloadCommand>> doPreprocessor(RequestData<DownloadCommand> requestData, ChannelHandlerContext ctx) {
        DownloadCommand command = requestData.getCommand();
        String allDir = DownloadDirUtils.composeDownloadDir(requestData.getApp(), requestData.getAgentServerInfos(), "all");
        if (Strings.isNullOrEmpty(allDir)) {
            throw new RuntimeException("No folders to download");
        }

        if (Strings.isNullOrEmpty(command.getPath())) {
            throw new RuntimeException("Down file path cannot be null or empty");
        }

        command.setDir(allDir);
        logger.info("{} download file [{}]", requestData.getUser(), command.getPath());
        return Optional.of(requestData);
    }

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_DOWNLOAD_FILE.getCode());
    }

    @Override
    public int getMinAgentVersion() {
        return 11;
    }

    @Override
    public boolean supportMulti() {
        return false;
    }
}
