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

import io.prometheus.client.Summary;
import qunar.tc.bistoury.serverside.metrics.BistouryMeter;

/**
 * @author leix.xie
 * @date 2019/7/8 15:48
 * @describe
 */
public class PrometheusBistouryMeter implements BistouryMeter {
    private final Summary.Child summary;

    public PrometheusBistouryMeter(final Summary summary, final String[] labels) {
        this.summary = summary.labels(labels);
    }

    @Override
    public void mark() {
        summary.observe(1);
    }

    @Override
    public void mark(final long n) {
        summary.observe(n);
    }
}
