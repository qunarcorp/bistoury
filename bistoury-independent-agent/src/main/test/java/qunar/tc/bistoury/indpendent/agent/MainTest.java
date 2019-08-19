package qunar.tc.bistoury.indpendent.agent;


import qunar.tc.bistoury.agent.AgentClient;

import java.io.IOException;

/**
 * @author xkrivzooh
 * @since 2019/8/18
 */
public class MainTest {

    public static void main(String[] args) throws IOException {
        System.setProperty("bistoury.proxy.host", "127.0.0.1:8080");
        Main.log();
        AgentClient instance = AgentClient.getInstance();
        instance.start();
        System.in.read();
    }

}