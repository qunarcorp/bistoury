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

package qunar.tc.bistoury.indpendent.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.AgentClient;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author zhenyu.nie created on 2018 2018/10/18 19:37
 */

/**
 * 如果Java版本高于java 9，启动agent需要在启动参数中添加--add-opens=java.base/jdk.internal.perf=ALL-UNNAMED
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
        logger.info(getString("OS Name:", System.getProperty("os.name")));
        logger.info(getString("OS Version:", System.getProperty("os.version")));
        logger.info(getString("Architecture:", System.getProperty("os.arch")));
        logger.info(getString("Java Home:", System.getProperty("java.home")));
        logger.info(getString("JVM Version:", System.getProperty("java.runtime.version")));
        logger.info(getString("JVM Vendor:", System.getProperty("java.vm.vendor")));
        logger.info(getString("CATALINA_BASE:", System.getProperty("catalina.base")));
        logger.info(getString("CATALINA_HOME:", System.getProperty("catalina.home")));
        for (String arg : args) {
            logger.info("Command line argument: {}", arg);
        }
    }


    private static String getString(String key, String value) {
        return String.format("%-23s%s", key, value);
    }
}
