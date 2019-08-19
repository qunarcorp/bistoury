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

package qunar.tc.bistoury.agent.task.monitor;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.util.AgentUtils;
import qunar.tc.bistoury.clientside.common.monitor.MetricsSnapshot;
import qunar.tc.bistoury.commands.arthas.telnet.DebugTelnetStore;
import qunar.tc.bistoury.commands.arthas.telnet.Telnet;
import qunar.tc.bistoury.commands.arthas.telnet.TelnetStore;
import qunar.tc.bistoury.commands.monitor.QMonitorStore;
import qunar.tc.bistoury.common.*;
import qunar.tc.bistoury.remoting.netty.MonitorReceiver;

/**
 * @author: leix.xie
 * @date: 2019/1/8 19:47
 * @describe：
 */
public class TaskRunner implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);
    private final QMonitorStore qMonitorStore;
    private final TelnetStore TELNET_STORE = DebugTelnetStore.getInstance();
    private MonitorReceiver receiver;
    private static final String COMMAND = BistouryConstants.REQ_MONITOR_SNAPSHOT;

    private static final String MIN_VERSION = "1.2.5";

    private String nullableAppCode;

    private static final TypeReference<TypeResponse<MetricsSnapshot>> TYPE_REFERENCE = new TypeReference<TypeResponse<MetricsSnapshot>>() {
    };

    TaskRunner(String nullableAppCode, MonitorReceiver receiver) {
        this.nullableAppCode = nullableAppCode;
        this.receiver = receiver;
        if (AgentUtils.supporGetPidFromProxy()) {
            this.qMonitorStore = QMonitorStore.getInstance(this.nullableAppCode);
        } else {
            this.qMonitorStore = QMonitorStore.getInstance(null);
        }
    }

    @Override
    public void run() {
        Telnet telnet = tryGetTelnet();
        if (telnet != null) {
            report(telnet);
        }
    }

    private void report(final Telnet telnet) {
        try {
            if (!legalVersion(telnet.getVersion())) {
                return;
            }
            telnet.write(COMMAND);
            telnet.read(COMMAND, receiver);
            storeMetricsSnapshot(receiver.getAndReset());
        } catch (Exception e) {
            logger.error("telnet write command error", e);
        } finally {
            telnet.close();
        }
    }

    private void storeMetricsSnapshot(final String content) {
        TypeResponse<MetricsSnapshot> typeResponse = JacksonSerializer.deSerialize(content, TYPE_REFERENCE);
        if (!BistouryConstants.REQ_MONITOR_SNAPSHOT.equals(typeResponse.getType())) {
            return;
        }
        CodeProcessResponse<MetricsSnapshot> responseData = typeResponse.getData();
        if (responseData.getCode() != 0) {
            logger.error("get metrics snapshot fail: {}", responseData.getMessage());
            return;
        }
        MetricsSnapshot snapshot = responseData.getData();
        qMonitorStore.store(snapshot);
    }

    private Telnet tryGetTelnet() {
        try {
            return TELNET_STORE.tryGetTelnet(this.nullableAppCode);
        } catch (Exception e) {
            logger.error("try get telnet fail", e);
            return null;
        }
    }

    private boolean legalVersion(final String version) {
        try {
            return VersionUtil.greaterEqualThanVersion(version, MIN_VERSION);
        } catch (Exception e) {
            return false;
        }
    }
}
