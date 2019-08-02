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

package qunar.tc.bistoury.metrics.prometheus;

import io.prometheus.client.Gauge;
import qunar.tc.bistoury.serverside.metrics.BistouryCounter;

/**
 * @author leix.xie
 * @date 2019/7/8 15:48
 * @describe
 */
public class PrometheusBistouryCounter implements BistouryCounter {
    private final Gauge.Child gauge;

    public PrometheusBistouryCounter(final Gauge gauge, final String[] labels) {
        this.gauge = gauge.labels(labels);
    }

    @Override
    public void inc() {
        gauge.inc();
    }

    @Override
    public void inc(final long n) {
        gauge.inc(n);
    }

    @Override
    public void dec() {
        gauge.dec();
    }

    @Override
    public void dec(final long n) {
        gauge.dec(n);
    }
}
