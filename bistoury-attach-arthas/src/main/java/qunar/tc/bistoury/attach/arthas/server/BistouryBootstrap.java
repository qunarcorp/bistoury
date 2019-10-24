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

package qunar.tc.bistoury.attach.arthas.server;

import com.taobao.arthas.core.config.Configure;
import com.taobao.arthas.core.shell.ShellServer;
import com.taobao.arthas.core.shell.ShellServerOptions;
import com.taobao.arthas.core.shell.command.CommandResolver;
import com.taobao.arthas.core.shell.handlers.BindHandler;
import com.taobao.arthas.core.shell.term.impl.TelnetTermServer;
import com.taobao.arthas.core.util.Constants;
import com.taobao.arthas.core.util.LogUtil;
import com.taobao.arthas.core.util.UserStatUtil;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClientStore;
import qunar.tc.bistoury.common.BistouryConstants;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2018 2018/11/19 19:49
 */
public class BistouryBootstrap {

    private static Logger logger = LogUtil.getArthasLogger();
    private static BistouryBootstrap bistouryBootstrap;

    private AtomicBoolean isBindRef = new AtomicBoolean(false);
    private int pid;
    private Instrumentation instrumentation;
    private Thread shutdown;
    private ShellServer shellServer;
    private ExecutorService executorService;

    private BistouryBootstrap(int pid, Instrumentation instrumentation) {
        this.pid = pid;
        this.instrumentation = instrumentation;

        executorService = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                final Thread t = new Thread(r, BistouryConstants.BISTOURY_COMMAND_THREAD_NAME);
                t.setDaemon(true);
                return t;
            }
        });

        shutdown = new Thread("bistoury-shutdown-hooker") {

            @Override
            public void run() {
                BistouryBootstrap.this.destroy();
            }
        };

        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    /**
     * Bootstrap bistoury server
     *
     * @param configure 配置信息
     * @throws IOException 服务器启动失败
     */
    public void bind(Configure configure) throws Throwable {

        long start = System.currentTimeMillis();

        if (!isBindRef.compareAndSet(false, true)) {
            throw new IllegalStateException("already bind");
        }

        try {
            InstrumentClientStore.init(instrumentation);

            ShellServerOptions options = new ShellServerOptions()
                    .setInstrumentation(instrumentation)
                    .setPid(pid)
                    .setWelcomeMessage(BistouryConstants.BISTOURY_VERSION_LINE_PREFIX + BistouryConstants.CURRENT_VERSION);
            shellServer = new ShellServerImpl(options, this);
            QBuiltinCommandPack builtinCommands = new QBuiltinCommandPack();
            List<CommandResolver> resolvers = new ArrayList<CommandResolver>();
            resolvers.add(builtinCommands);
            // TODO: discover user provided command resolver
            shellServer.registerTermServer(new TelnetTermServer(
                    configure.getIp(), configure.getTelnetPort(), options.getConnectionTimeout()));

            for (CommandResolver resolver : resolvers) {
                shellServer.registerCommandResolver(resolver);
            }

            shellServer.listen(new BindHandler(isBindRef));

            logger.info("bistoury-server listening on network={};telnet={};timeout={};", (Object) configure.getIp(),
                    configure.getTelnetPort(), options.getConnectionTimeout());

            logger.info("bistoury-server started in {} ms", System.currentTimeMillis() - start );
        } catch (Throwable e) {
            logger.error(null, "Error during bind to port " + configure.getTelnetPort(), e);
            if (shellServer != null) {
                shellServer.close();
            }

            InstrumentClientStore.destroy();

            isBindRef.compareAndSet(true, false);
            throw e;
        }
    }

    /**
     * 判断服务端是否已经启动
     *
     * @return true:服务端已经启动;false:服务端关闭
     */
    public boolean isBind() {
        return isBindRef.get();
    }

    public void destroy() {
        executorService.shutdownNow();
        UserStatUtil.destroy();
        // clear the reference in Spy class.
        cleanUpSpyReference();
        try {
            Runtime.getRuntime().removeShutdownHook(shutdown);
        } catch (Throwable t) {
            // ignore
        }
        logger.info("bistoury-server destroy completed.");
        // see middleware-container/arthas/issues/123
        try {
            LogUtil.closeResultLogger();
        } catch (Throwable e) {
            logger.error("qlogger-001", "close logger error", e);
        }

        try {
            // 如果日志实现是log4j的话
            closeResultLog4jLogger();
        } catch (Throwable e) {
            logger.error("qlogger-002", "close log4j logger error", e);
        }
    }

    private void closeResultLog4jLogger() throws Exception {
        String name = LogUtil.getResultLogger().getName();
        Class<?> logManagerClass = Class.forName("org.apache.log4j.LogManager");
        Method getLogger = logManagerClass.getDeclaredMethod("getLogger", String.class);
        Object resultLog4jLogger = getLogger.invoke(null, name);
        if (resultLog4jLogger != null) {
            // 这里要设置一下，不然可能会在关闭的时候打印个interrupt信息
            Class<?> logHelper = Class.forName("org.apache.log4j.helpers.LogLog");
            Method setQuietMode = logHelper.getMethod("setQuietMode", boolean.class);
            setQuietMode.invoke(null, true);

            Method removeAllAppenders = resultLog4jLogger.getClass().getMethod("removeAllAppenders");
            removeAllAppenders.invoke(resultLog4jLogger);
        }
    }

    /**
     * 单例
     *
     * @param instrumentation JVM增强
     * @return BistouryServer单例
     */
    public synchronized static BistouryBootstrap getInstance(int javaPid, Instrumentation instrumentation) {
        if (bistouryBootstrap == null) {
            bistouryBootstrap = new BistouryBootstrap(javaPid, instrumentation);
        }
        return bistouryBootstrap;
    }

    /**
     * @return BistouryServer单例
     */
    public static BistouryBootstrap getInstance() {
        if (bistouryBootstrap == null) {
            throw new IllegalStateException("BistouryBootstrap must be initialized before!");
        }
        return bistouryBootstrap;
    }

    public void execute(Runnable command) {
        executorService.execute(command);
    }

    /**
     * 清除spy中对classloader的引用，避免内存泄露
     */
    private void cleanUpSpyReference() {
        try {
            spyDestroy(Constants.SPY_CLASSNAME);
        } catch (ClassNotFoundException e) {
            logger.error(null, "arthas spy load failed from BistouryClassLoader, which should not happen", e);
        } catch (Exception e) {
            logger.error(null, "arthas spy destroy failed: ", e);
        }

        try {
            spyDestroy(BistouryConstants.SPY_CLASSNAME);
        } catch (ClassNotFoundException e) {
            logger.error(null, "bistoury spy load failed from BistouryClassLoader, which should not happen", e);
        } catch (Exception e) {
            logger.error(null, "bistoury spy destroy failed: ", e);
        }
    }

    private void spyDestroy(String spyClassname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> spyClass = this.getClass().getClassLoader().loadClass(spyClassname);
        Method agentDestroyMethod = spyClass.getMethod("destroy");
        agentDestroyMethod.invoke(null);
    }
}
