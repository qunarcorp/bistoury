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

package qunar.tc.bistoury.agent.common.pid;


import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.AgentConstants;
import qunar.tc.bistoury.agent.common.pid.impl.PidByJpsHandler;
import qunar.tc.bistoury.agent.common.pid.impl.PidByPsHandler;
import qunar.tc.bistoury.agent.common.pid.impl.PidBySystemPropertyHandler;
import qunar.tc.bistoury.agent.common.util.AgentUtils;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;

import java.util.*;

/**
 * @author: leix.xie
 * @date: 2019/3/7 15:21
 * @describe：
 */
public class PidUtils {

    private static final Logger logger = LoggerFactory.getLogger(PidUtils.class);

    private static final List<PidHandler> PID_HANDLERS = initPidHandler();

    private static List<PidHandler> initPidHandler() {
        List<PidHandler> handlers = Lists.newArrayList();

        handlers.add(new PidBySystemPropertyHandler());

        if (Boolean.parseBoolean(System.getProperty("bistoury.pid.handler.jps.enable", "true"))) {
            handlers.add(new PidByJpsHandler());
        }

        if (Boolean.parseBoolean(System.getProperty("bistoury.pid.handler.ps.enable", "true"))) {
            handlers.add(new PidByPsHandler());
        }

        ServiceLoader.load(PidHandlerFactory.class).forEach(factory -> handlers.add(factory.create()));
        handlers.sort(Comparator.comparingInt(PidHandler::priority));
        return ImmutableList.copyOf(handlers);
    }

    private static int getPid() {
        for (PidHandler handler : PID_HANDLERS) {
            int pid = handler.getPid();
            if (pid > 0) {
                logger.info("get pid by {} success, pid is {}", handler.getClass().getSimpleName(), pid);
                return pid;
            }
        }
        return -1;
    }


    public static int getPid(String nullableAppCode) {
        if (!AgentUtils.supporGetPidFromProxy()) {
            return getPid();
        }
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nullableAppCode), "appCode不能为空");
        MetaStore appMetaStore = MetaStores.getAppMetaStore(nullableAppCode);
        return appMetaStore.getIntProperty(AgentConstants.PID);
    }

}