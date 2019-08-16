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

package qunar.tc.bistoury.agent.common.task;

import com.google.common.collect.ImmutableList;
import qunar.tc.bistoury.agent.common.pid.PidUtils;
import qunar.tc.bistoury.agent.common.task.AgentGlobalTaskFactory;

import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/1/8 17:15
 */
public class AgentGlobalTaskInitializer {

    private static boolean init = false;

    public static synchronized void init() {
        if (!init) {
            List<AgentGlobalTaskFactory> tasks = ImmutableList.copyOf(ServiceLoader.load(AgentGlobalTaskFactory.class));

            Set<String> agentServerAppCodes = PidUtils.getAgentServerAppCodes();
            for (String appCode : agentServerAppCodes) {
                for (AgentGlobalTaskFactory task : tasks) {
                    task.start(appCode);
                }
            }
            init = true;
        }
    }
}
