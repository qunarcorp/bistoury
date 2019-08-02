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

package qunar.tc.bistoury.instrument.client.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.google.common.primitives.Doubles;
import qunar.tc.bistoury.clientside.common.monitor.MetricType;
import qunar.tc.bistoury.instrument.client.metrics.adapter.ResettableTimer;
import qunar.tc.bistoury.instrument.client.metrics.adapter.StatsBuffer;

public class ItemValue extends Item {

    @SuppressWarnings("unchecked")
    static float[] valueOf(MetricType type, Metric value) {

        switch (type) {
            case COUNTER:
                Counter counter = (com.codahale.metrics.Counter) value;
                return new float[]{value(counter.getCount())};
            case TIMER:
                ResettableTimer resettableTimer = (ResettableTimer) value;
                StatsBuffer buffer = resettableTimer.getBuffer();
                buffer.computeStats();
                double[] percentileValues = buffer.getPercentileValues();
                double[] percentiles = buffer.getPercentiles();
                float p98 = value(percentileValues[percentilesIndex(ResettableTimer.P98, percentiles)]);
                buffer.reset();
                return new float[]{value(resettableTimer.getOneMinuteRate()),
                        p98};
        }
        throw new IllegalArgumentException("invalid metric");
    }

    private static int percentilesIndex(double percentile, double[] percentiles) {
        int indexOf = Doubles.indexOf(percentiles, percentile);
        return indexOf == -1 ? 0 : indexOf;
    }

    static float value(double value) {
        return (float) value;
    }
}
