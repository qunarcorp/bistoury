package qunar.tc.bistoury.indpendent.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.AgentClient;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author zhenyu.nie created on 2018 2018/10/18 19:37
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log();
        AgentClient instance = AgentClient.getInstance();
        instance.start();
        System.in.read();
    }

    public static void log() {
        List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : args) {
            logger.info("Command line argument: {}", arg);
        }
    }
}
