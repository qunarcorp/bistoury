/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    private final String nullableCode;

    public ArthasTask(TelnetStore telnetStore, String id, long maxRunningMs,
                      int pid, String command, ResponseHandler handler, String nullableAppCode) {
        this.telnetStore = telnetStore;
        this.id = id;
        this.maxRunningMs = maxRunningMs;
        this.pid = pid;
        this.command = command;
        this.handler = handler;
        this.nullableCode = nullableAppCode;
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

                Telnet telnet = telnetStore.getTelnet(nullableCode, pid);
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
            client = telnetStore.tryGetTelnet(nullableCode);
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
