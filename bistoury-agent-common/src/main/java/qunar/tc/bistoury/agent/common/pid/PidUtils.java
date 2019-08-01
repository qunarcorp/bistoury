package qunar.tc.bistoury.agent.common.pid;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.pid.impl.PidByJpsHandler;
import qunar.tc.bistoury.agent.common.pid.impl.PidByPsHandler;
import qunar.tc.bistoury.agent.common.pid.impl.PidBySystemPropertyHandler;

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

        ServiceLoader.load(PidHandlerFactory.class).forEach(factory -> handlers.add(factory.create()));
        handlers.sort(Comparator.comparingInt(PidHandler::priority));
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