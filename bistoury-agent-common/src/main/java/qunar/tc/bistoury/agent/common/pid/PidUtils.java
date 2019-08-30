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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.pid.impl.PidByJpsHandler;
import qunar.tc.bistoury.agent.common.pid.impl.PidByPsHandler;
import qunar.tc.bistoury.agent.common.pid.impl.PidBySystemPropertyHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

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

        ServiceLoader<PidHandlerFactory> handlerFactories = ServiceLoader.load(PidHandlerFactory.class);
        for (PidHandlerFactory factory : handlerFactories) {
            handlers.add(factory.create());
        }
        Collections.sort(handlers, new Comparator<PidHandler>() {
            @Override
            public int compare(PidHandler o1, PidHandler o2) {
                return Integer.compare(o1.priority(), o2.priority());
            }
        });
        return ImmutableList.copyOf(handlers);
    }

    public static int getPid() {
        for (PidHandler handler : PID_HANDLERS) {
            int pid = handler.getPid();
            if (pid > 0) {
                logger.info("get pid by {} success, pid is {}", handler.getClass().getSimpleName(), pid);
                return pid;
            }
        }
        return -1;
    }
}