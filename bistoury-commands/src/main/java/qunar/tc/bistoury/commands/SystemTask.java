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

package qunar.tc.bistoury.commands;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ClosableProcess;
import qunar.tc.bistoury.agent.common.ClosableProcesses;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @author zhenyu.nie created on 2018 2018/10/9 12:12
 */
public class SystemTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(SystemTask.class);

    private static final ListeningExecutorService agentExecutor = AgentRemotingExecutor.getExecutor();

    private final String id;

    private final ProcessBuilder processBuilder;

    private final ResponseHandler handler;

    private final long maxRunningMs;

    private volatile ClosableProcess process;

    private volatile ListenableFuture<Integer> future;

    public SystemTask(String id,
                      String command,
                      String presentWorkDir,
                      ResponseHandler handler,
                      long maxRunningMs) {
        this.id = id;
        String realCommand = CustomScript.replaceScriptPath(command);
        this.processBuilder = new ProcessBuilder()
                .directory(new File(presentWorkDir)).redirectErrorStream(true).command("/bin/bash", "-c", realCommand);
        this.handler = handler;
        this.maxRunningMs = maxRunningMs;
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
        future = agentExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                process = ClosableProcesses.wrap(processBuilder.start());
                return process.readAndWaitFor(handler);
            }
        });
        return future;
    }

    @Override
    public void cancel() {
        if (future == null || future.isDone()) {
            return;
        }

        try {
            if (process != null) {
                process.destroy();
                process = null;
            }
        } catch (Exception e) {
            logger.error("destroy system task error", e);
        }

        try {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        } catch (Exception e) {
            logger.error("destroy system task error", e);
        }
    }
}
