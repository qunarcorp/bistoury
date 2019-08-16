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

package qunar.tc.bistoury.agent.task.agentInfo;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import qunar.tc.bistoury.agent.common.task.AgentGlobalTaskFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.util.concurrent.Executors;

/**
 * @author: leix.xie
 * @date: 2019/2/25 17:55
 * @describe：
 */
public class AgentInfoPushTaskFactory implements AgentGlobalTaskFactory {
    private static final ListeningScheduledExecutorService executor = MoreExecutors.listeningDecorator(
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("agent-info-push-task", true)));

    @Override
    public void start(String appCode) {
        TaskRunner taskRunner = new TaskRunner(appCode, executor);
        executor.submit(taskRunner);
    }
}
