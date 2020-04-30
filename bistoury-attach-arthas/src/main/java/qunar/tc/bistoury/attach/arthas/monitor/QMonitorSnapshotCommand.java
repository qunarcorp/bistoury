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

import com.google.common.base.Strings;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Option;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.AttachJacksonSerializer;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.clientside.common.monitor.MetricsSnapshot;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.CodeProcessResponse;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.common.URLCoder;

/**
 * @author: leix.xie
 * @date: 2019/1/9 11:40
 * @describe：
 */
@Name(BistouryConstants.REQ_MONITOR_SNAPSHOT)
public class QMonitorSnapshotCommand extends AnnotatedCommand {
    private static final Logger logger = BistouryLoggger.getLogger();

    private String name;


    @Option(shortName = "n", longName = "name")
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void process(CommandProcess process) {
        logger.debug("receive monitor snapshot command");
        CodeProcessResponse<MetricsSnapshot> response = new CodeProcessResponse<>();
        TypeResponse<MetricsSnapshot> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_MONITOR_SNAPSHOT);
        typeResponse.setData(response);
        try {
            final QMonitorClient monitorClient = QMonitorClients.getInstance();
            MetricsSnapshot snapshot = monitorClient.reportMonitor(Strings.nullToEmpty(this.name));
            response.setData(snapshot);
            response.setCode(0);
        } catch (Throwable e) {
            response.setCode(-1);
            response.setMessage("qmonitor snapshot get error, " + e.getClass() + ", " + e.getMessage());
        } finally {
            process.write(URLCoder.encode(AttachJacksonSerializer.serialize(typeResponse)));
            process.end();
            logger.debug("finish monitor snapshot command");
        }
    }
}
