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

import com.google.common.base.Strings;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.taobao.arthas.core.config.Configure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author zhenyu.nie created on 2018 2018/11/19 19:12
 */
public class ArthasStarter {

    private static final Logger logger = LoggerFactory.getLogger(ArthasStarter.class);

    private static final String DEFAULT_AGENT_JAR_PATH;
    private static final String DEFAULT_CORE_JAR_PATH;

    static {
        String libDirPath = System.getProperty("bistoury.lib.dir");
        File libDir;
        if (Strings.isNullOrEmpty(libDirPath)) {
            libDir = new File(Configure.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
        } else {
            libDir = new File(libDirPath);
        }
        DEFAULT_AGENT_JAR_PATH = new File(libDir, "bistoury-instrument-agent.jar").getPath();
        DEFAULT_CORE_JAR_PATH = new File(libDir, "arthas-core.jar").getPath();
    }

    public synchronized static void start(int pid) throws Exception {
        Configure configure = getConfigure(pid);
        attachAgent(configure);
    }

    private static void attachAgent(Configure configure) throws Exception {
        logger.info("start attach to arthas agent");
        VirtualMachineDescriptor virtualMachineDescriptor = null;
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            String pid = descriptor.id();
            if (pid.equals(String.valueOf(configure.getJavaPid()))) {
                virtualMachineDescriptor = descriptor;
            }
        }
        VirtualMachine virtualMachine = null;
        try {
            if (virtualMachineDescriptor == null) {
                virtualMachine = VirtualMachine.attach(String.valueOf(configure.getJavaPid()));
            } else {
                virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
            }

            Properties targetSystemProperties = virtualMachine.getSystemProperties();
            String targetJavaVersion = targetSystemProperties.getProperty("java.specification.version");
            String currentJavaVersion = System.getProperty("java.specification.version");
            if (targetJavaVersion != null && currentJavaVersion != null) {
                if (!targetJavaVersion.equals(currentJavaVersion)) {
                    logger.warn("Current VM java version: {} do not match target VM java version: {}, attach may fail.",
                            currentJavaVersion, targetJavaVersion);
                    logger.warn("Target VM JAVA_HOME is {}, try to set the same JAVA_HOME.",
                            targetSystemProperties.getProperty("java.home"));
                }
            }

            String arthasAgent = configure.getArthasAgent();
            File agentFile = new File(arthasAgent);
            String name = agentFile.getName();
            String prefix = name.substring(0, name.indexOf('.'));
            File dir = agentFile.getParentFile();
            File realAgentFile = getFileWithPrefix(dir, prefix);

            logger.info("start load arthas agent, input {}, load {}", arthasAgent, realAgentFile.getCanonicalPath());
            final String delimiter = "$|$";
            virtualMachine.loadAgent(realAgentFile.getCanonicalPath(),
                    configure.getArthasCore() + delimiter + ";;" + configure.toString() + delimiter + System.getProperty("bistoury.app.lib.class"));
        } finally {
            if (virtualMachine != null) {
                virtualMachine.detach();
            }
        }
    }

    private static File getFileWithPrefix(File dir, final String prefix) {
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix);
            }
        });
        if (files == null || files.length != 1) {
            throw new IllegalStateException("get files, " + Arrays.toString(files));
        }
        return files[0];
    }

    private static Configure getConfigure(int pid) {
        String agentJar = System.getProperty("bistoury.agent.jar.path", DEFAULT_AGENT_JAR_PATH);
        String coreJar = System.getProperty("bistoury.arthas.core.jar.path", DEFAULT_CORE_JAR_PATH);

        Configure configure = new Configure();
        configure.setJavaPid(pid);
        configure.setArthasAgent(agentJar);
        configure.setArthasCore(coreJar);
        configure.setIp(TelnetConstants.TELNET_CONNECTION_IP);
        configure.setTelnetPort(TelnetConstants.TELNET_CONNECTION_PORT);
        configure.setHttpPort(TelnetConstants.DEFAULT_HTTP_PORT);
        return configure;
    }
}
