package qunar.tc.bistoury.commands.arthas;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.commands.arthas.telnet.Telnet;
import qunar.tc.bistoury.commands.arthas.telnet.TelnetStore;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.util.concurrent.Callable;

/**
 * @author zhenyu.nie created on 2018 2018/10/15 18:55
 */
public class ArthasTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(ArthasTask.class);

    private static final ListeningExecutorService agentExecutor = AgentRemotingExecutor.getExecutor();

    private final TelnetStore telnetStore;

    private final String id;

    private final long maxRunningMs;

    private final int pid;

    private final String command;

    private final ResponseHandler handler;

    private volatile ListenableFuture<Integer> future;

    public ArthasTask(TelnetStore telnetStore, String id, long maxRunningMs, int pid, String command, ResponseHandler handler) {
        this.telnetStore = telnetStore;
        this.id = id;
        this.maxRunningMs = maxRunningMs;
        this.pid = pid;
        this.command = command;
        this.handler = handler;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getMaxRunningMs() {
        return maxRunningMs;
    }

    @Override
    public ListenableFuture<Integer> execute() {
        this.future = agentExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                if (actShutDownCommand()) {
                    return 0;
                }

                Telnet telnet = telnetStore.getTelnet(pid);
                try {
                    telnet.write(command);
                    telnet.read(command, handler);
                    return 0;
                } finally {
                    telnet.close();
                }
            }
        });
        return future;
    }

    @Override
    public void cancel() {
        try {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        } catch (Exception e) {
            logger.error("cancel arthas task error", e);
        }
    }

    private boolean actShutDownCommand() {
        if (!BistouryConstants.SHUTDOWN_COMMAND.equals(command)) {
            return false;
        }

        Telnet client = null;
        try {
            client = telnetStore.tryGetTelnet();
            if (client != null) {
                client.write(BistouryConstants.SHUTDOWN_COMMAND);
            }
        } catch (Exception e) {
            // ignore
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return true;
    }
}
