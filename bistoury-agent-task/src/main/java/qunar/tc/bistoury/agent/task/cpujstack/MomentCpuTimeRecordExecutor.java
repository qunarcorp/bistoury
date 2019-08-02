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

package qunar.tc.bistoury.agent.task.cpujstack;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import qunar.tc.bistoury.agent.task.proc.ProcessStateCalculator;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen
 * @date 19-1-22
 */
public class MomentCpuTimeRecordExecutor implements PidRecordExecutor {

    private static final int intervalMillis = 4000;

    private final ListeningScheduledExecutorService listeningScheduledExecutorService;
    private final ProcessStateCalculator processStateCalculator = ProcessStateCalculator.getInstance();

    public MomentCpuTimeRecordExecutor(ListeningScheduledExecutorService scheduledExecutorService) {
        this.listeningScheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public ListenableFuture<Map<Integer, Double>> execute(final int pid) {
        processStateCalculator.startRecordFullStat(pid);
        return listeningScheduledExecutorService.schedule(new Callable<Map<Integer, Double>>() {
            @Override
            public Map<Integer, Double> call() {
                return processStateCalculator.endRecordFullStat(pid);
            }
        }, intervalMillis, TimeUnit.MILLISECONDS);
    }
}
