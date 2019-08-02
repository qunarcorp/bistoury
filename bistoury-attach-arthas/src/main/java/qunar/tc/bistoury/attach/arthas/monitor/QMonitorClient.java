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

package qunar.tc.bistoury.attach.arthas.monitor;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClient;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.clientside.common.monitor.MetricsSnapshot;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;
import qunar.tc.bistoury.instrument.client.metrics.Metrics;
import qunar.tc.bistoury.instrument.client.metrics.MetricsReportor;
import qunar.tc.bistoury.instrument.client.metrics.QMonitorMetricsReportor;
import qunar.tc.bistoury.instrument.client.monitor.DefaultMonitor;
import qunar.tc.bistoury.instrument.client.monitor.Monitor;

/**
 * @author: leix.xie
 * @date: 2018/12/26 20:39
 * @describe：
 */
public class QMonitorClient implements InstrumentClient {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final MetricsReportor REPORTOR = new QMonitorMetricsReportor(Metrics.INSTANCE);

    private final Monitor monitor;

    public QMonitorClient(InstrumentInfo instrumentInfo) {
        logger.info("start init qmonitor client");
        try {
            Monitor monitor = new DefaultMonitor();
            monitor.startup(instrumentInfo);

            this.monitor = monitor;
            logger.info("init qmonitor client success");
        } catch (Throwable e) {
            destroy();
            logger.error("", "error init qmonitor client", e);
            throw new IllegalStateException("qmonitor client init error", e);
        }
    }

    public String addMonitor(String source, int line) {
        return monitor.addMonitor(source, line);
    }

    public MetricsSnapshot reportMonitor(final String name) {
        return REPORTOR.report(name);
    }

    public synchronized void destroy() {
        try {
            logger.info("start destroy qmonitorclient");
            Metrics.destroy();
            if (monitor != null) {
                monitor.destroy();
            }
            logger.info("end destroy qmonitorclient");
        } catch (Exception e) {
            logger.error("", "destroy qmonitorclient error", e);
        }
    }
}
