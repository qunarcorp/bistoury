package qunar.tc.bistoury.proxy.communicate.agent.handler;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.remoting.protocol.Datagram;

import java.io.*;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static qunar.tc.bistoury.common.BistouryConstants.PROFILER_ROOT_AGENT_PATH;
import static qunar.tc.bistoury.common.BistouryConstants.PROFILER_ROOT_TEMP_PATH;
import static qunar.tc.bistoury.remoting.protocol.CommandCode.*;

/**
 * @author cai.wen created on 19-12-11 上午11:28
 */
@Service
public class AgentProfilerFileProcessor implements AgentMessageProcessor {

    private final ExecutorService writeFileExecutor = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setDaemon(true)
                    .setNameFormat("write-profiler-file").build());

    private static final Logger logger = LoggerFactory.getLogger(AgentProfilerFileProcessor.class);

    private final LoadingCache<String, OutputStream> fileStreamCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .removalListener(this::close)
            .build(getCacheLoader());

    private CacheLoader<String, OutputStream> getCacheLoader() {
        return new CacheLoader<String, OutputStream>() {
            @Override
            public OutputStream load(String filePath) throws Exception {
                File path = new File(filePath);
                path.getParentFile().mkdirs();
                path.createNewFile();
                return new BufferedOutputStream(new FileOutputStream(filePath));
            }
        };
    }

    private void close(RemovalNotification<String, OutputStream> notification) {
        String filePath = notification.getKey();
        OutputStream outputStream = notification.getValue();
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            logger.error("close file from agent error. path: {}", filePath, e);
        }
    }

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(
                REQ_TYPE_PROFILER_FILE.getCode(),
                REQ_TYPE_PROFILER_FILE_END.getCode(),
                REQ_TYPE_PROFILER_FILE_ERROR.getCode(),
                REQ_TYPE_PROFILER_ALL_FILE_END.getCode());
    }

    @Override
    public void process(ChannelHandlerContext ctx, Datagram message) {
        writeFileExecutor.submit(() -> {
            final int code = message.getHeader().getCode();
            try {
                if (code == REQ_TYPE_PROFILER_FILE.getCode()) {
                    String path = getWritePath(getRelativeFileDir(message.getBody()));
                    writeToFile(path, message.getBody());
                } else if (code == REQ_TYPE_PROFILER_FILE_END.getCode()) {
                    String path = getWritePath(getRelativeFileDir(message.getBody()));
                    closeFileStream(path);
                    logger.info("write profiler file end. file: {}", path);
                } else if (code == REQ_TYPE_PROFILER_FILE_ERROR.getCode()) {
                    logger.warn("request file error. result msg: {}", new String(getContent(message.getBody()), Charsets.UTF_8));
                } else if (code == REQ_TYPE_PROFILER_ALL_FILE_END.getCode()) {
                    String path = getWritePath(getRelativeFileDir(message.getBody()));
                    renamePath(path);
                    logger.info("write profiler file all end. file: {}", path);
                }
            } catch (Exception e) {
                logger.error("process receive agent file error. message: {}", message, e);
            }
        });
    }

    private void renamePath(String tempFile) {
        File tempDir = new File(tempFile);
        new File(PROFILER_ROOT_AGENT_PATH).mkdirs();
        new File(PROFILER_ROOT_TEMP_PATH).mkdirs();
        String realProfilerPath = PROFILER_ROOT_TEMP_PATH + File.separator + tempDir.getName();
        tempDir.renameTo(new File(realProfilerPath));
    }

    private void closeFileStream(String filePath) throws IOException, ExecutionException {
        fileStreamCache.get(filePath).close();
        fileStreamCache.invalidate(filePath);
    }

    private void writeToFile(String filePath, ByteBuf byteBuf) throws IOException, ExecutionException {
        fileStreamCache.get(filePath).write(getContent(byteBuf));
    }

    private byte[] getContent(ByteBuf byteBuf) {
        int length = byteBuf.readableBytes();
        byte[] chunk = new byte[length];
        byteBuf.readBytes(chunk);
        return chunk;
    }

    private String getWritePath(String fileName) {
        return PROFILER_ROOT_AGENT_PATH + File.separator + fileName;
    }

    private String getRelativeFileDir(ByteBuf byteBuf) {
        byte[] profilerDirBytes = new byte[128];
        byte[] nameBytes = new byte[128];
        byteBuf.readBytes(profilerDirBytes);
        byteBuf.readBytes(nameBytes);
        return getRealString(profilerDirBytes) + File.separator + getRealString(nameBytes);
    }

    private String getRealString(byte[] byteArray) {
        int trimLength = byteArray.length;
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] == 0) {
                trimLength = i;
                break;
            }
        }
        return new String(Arrays.copyOf(byteArray, trimLength), Charsets.UTF_8);
    }
}
