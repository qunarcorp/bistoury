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

package qunar.tc.bistoury.agent.task.heapHisto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.config.AgentConfig;
import qunar.tc.bistoury.agent.common.pid.PidUtils;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.commands.heapHisto.HeapHistoBeanHandle;
import qunar.tc.bistoury.commands.heapHisto.HeapHistoStore;
import qunar.tc.bistoury.commands.heapHisto.HistogramBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author: leix.xie
 * @date: 2019/4/1 10:16
 * @describe：
 */
public class TaskRunner implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    private static final AgentConfig agentConfig = new AgentConfig(MetaStores.getMetaStore());

    private HeapHistoStore heapHistoStore = HeapHistoStore.getInstance();

    @Override
    public void run() {
        boolean heapJMapHistoOn = agentConfig.isHeapHistoOn();
        if (heapJMapHistoOn) {
            report();
        }
    }

    private void report() {
        try {
            int pid = PidUtils.getPid();
            HeapHistoBeanHandle heapHistoBeanHandle = new HeapHistoBeanHandle("-all", pid);
            List<HistogramBean> histogramBeans = heapHistoBeanHandle.heapHisto();

            Collections.sort(histogramBeans, new Comparator<HistogramBean>() {
                @Override
                public int compare(HistogramBean a, HistogramBean b) {
                    return Long.compare(b.getBytes(), a.getBytes());
                }
            });

            List<HistogramBean> result;
            if (histogramBeans.size() <= agentConfig.getHeapHistoStoreSize()) {
                result = histogramBeans;
            } else {
                result = histogramBeans.subList(0, agentConfig.getHeapHistoStoreSize());
            }
            heapHistoStore.store(result);
        } catch (Exception e) {
            logger.error("heap histo dump error", e);
        }
    }
}
