package qunar.tc.bistoury.commands.job;

import com.google.common.util.concurrent.SettableFuture;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.commands.arthas.telnet.CommunicateUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author zhenyu.nie created on 2019 2019/10/28 14:19
 */
public abstract class BytesJob implements ContinueResponseJob {

    private final String id;

    private final ResponseHandler handler;

    private final SettableFuture<Integer> future;

    private InputStream inputStream;

    protected BytesJob(String id, ResponseHandler handler, SettableFuture<Integer> future) {
        this.id = id;
        this.handler = handler;
        this.future = future;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void init() throws Exception {
        byte[] bytes = getBytes();
        inputStream = new ByteArrayInputStream(bytes);
    }

    protected abstract byte[] getBytes() throws Exception;

    @Override
    public boolean doResponse() throws Exception {
        byte[] bytes = new byte[CommunicateUtil.DEFAULT_BUFFER_SIZE];
        int count = inputStream.read(bytes);
        if (count == CommunicateUtil.DEFAULT_BUFFER_SIZE) {
            handler.handle(bytes);
        } else if (count > 0) {
            handler.handle(Arrays.copyOf(bytes, count));
        }
        return count == -1;
    }

    @Override
    public void finish() {
        future.set(0);
    }

    @Override
    public void error(Throwable t) {
        future.setException(t);
    }
}
