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

package qunar.tc.bistoury.instrument.client.metrics.adapter;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.google.common.primitives.Ints;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 15-5-7
 * Time: 下午2:02
 */
public class ResettableTimer implements Metric {
    private static final Logger LOG = BistouryLoggger.getLogger();

    public static final double P98 = 98.0;

    private static final int DEFAULT_TIMER_SIZE = 8000;
    private static final double[] DEFAULT_PER = new double[]{P98};

    private final Meter meter;
    private final StatsBuffer timer;

    public ResettableTimer() {
        this(Clock.defaultClock(), DEFAULT_PER, DEFAULT_TIMER_SIZE);
    }

    public ResettableTimer(Clock clock, double[] percentiles, int timerSize) {
        this.meter = new Meter(clock);
        this.timer = new StatsBuffer(timerSize, percentiles);
    }

    public void update(long el, TimeUnit timeUnit) {
        meter.mark();
        long time = timeUnit.toMillis(el);
        try {
            timer.record(Ints.checkedCast(time));
        } catch (IllegalArgumentException e) {
            LOG.debug("update timer failed.", e);
        }
    }

    public double getFifteenMinuteRate() {
        return meter.getFifteenMinuteRate();
    }

    public double getFiveMinuteRate() {
        return meter.getFiveMinuteRate();
    }

    public double getMeanRate() {
        return meter.getMeanRate();
    }

    public double getOneMinuteRate() {
        return meter.getOneMinuteRate();
    }

    public StatsBuffer getBuffer() {
        return timer;
    }
}
