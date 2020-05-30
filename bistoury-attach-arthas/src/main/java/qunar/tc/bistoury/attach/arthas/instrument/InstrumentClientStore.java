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

package qunar.tc.bistoury.attach.arthas.instrument;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.config.AppConfigClients;
import qunar.tc.bistoury.attach.arthas.debug.JarDebugClients;
import qunar.tc.bistoury.attach.arthas.debug.QDebugClients;
import qunar.tc.bistoury.attach.arthas.jar.JarInfoClients;
import qunar.tc.bistoury.attach.arthas.monitor.QMonitorClients;
import qunar.tc.bistoury.attach.arthas.profiler.GProfilerClients;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.classpath.AppClassPathSupplier;
import qunar.tc.bistoury.instrument.client.classpath.AppLibClassSupplier;
import qunar.tc.bistoury.instrument.client.classpath.DefaultAppClassPathSupplier;
import qunar.tc.bistoury.instrument.client.classpath.DefaultAppLibClassSupplier;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhenyu.nie created on 2019 2019/2/18 14:35
 */
public class InstrumentClientStore {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static List<InstrumentClient> clients = ImmutableList.of();

    private static ReentrantLock lock = new ReentrantLock();

    private static boolean init = false;

    private static InstrumentInfo instrumentInfo;

    public static synchronized void init(final Instrumentation instrumentation) {
        if (init) {
            return;
        }

        init = true;

        AppLibClassSupplier appLibClassSupplier = new AppLibClassSupplier() {
            private final Supplier<Class<?>> memoize = Suppliers.memoize(new DefaultAppLibClassSupplier(instrumentation));

            @Override
            public Class<?> get() {
                return memoize.get();
            }
        };

        AppClassPathSupplier appClassPathSupplier = new DefaultAppClassPathSupplier(appLibClassSupplier);

        instrumentInfo = new InstrumentInfo(
                instrumentation,
                lock,
                appLibClassSupplier,
                appClassPathSupplier,
                DefaultClassFileBuffer.getInstance());

        ImmutableList.Builder<InstrumentClient> builder = new ImmutableList.Builder<>();

        //jar debug放在最前面，因为在里面会对jar包启动的项目进行解压
        try {
            builder.add(JarDebugClients.create(instrumentInfo));
        } catch (Exception e) {
            logger.error("", "jar decompiler init error", e);
        }

        try {
            builder.add(AppConfigClients.create(instrumentInfo));
        } catch (Exception e) {
            logger.error("", "app config client init error", e);
        }

        try {
            builder.add(QDebugClients.create(instrumentInfo));
        } catch (Exception e) {
            logger.error("", "qdebug client init error", e);
        }
        try {
            builder.add(QMonitorClients.create(instrumentInfo));
        } catch (Exception e) {
            logger.error("", "qmonitor client init error", e);
        }

        try {
            builder.add(JarInfoClients.create(instrumentInfo));
        } catch (Exception e) {
            logger.error("", "jar info client init error", e);
        }

        try {
            builder.add(GProfilerClients.create());
        } catch (Exception e) {
            logger.error("", "profiler client init error", e);
        }

        clients = builder.build();
    }

    public static synchronized void destroy() {
        try {
            if (instrumentInfo != null) {
                instrumentInfo.reset();

                for (InstrumentClient client : clients) {
                    client.destroy();
                }
            }
        } catch (Exception e) {
            logger.error("", "destroy error", e);
        }
    }
}
