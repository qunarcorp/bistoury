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

import java.util.concurrent.atomic.AtomicLong;

class DeltaCounter extends com.codahale.metrics.Counter implements Delta {

    // last, value
    private AtomicLong[] value = new AtomicLong[2];
    // keep value if delta=0?
    private final boolean keep;

    public DeltaCounter(boolean keep) {
        this.keep = keep;
        value[0] = new AtomicLong();
        value[1] = new AtomicLong();
    }

    @Override
    public void tick() {
        long cur_cnt = super.getCount();
        long last_cnt = value[0].get();

        long delta = cur_cnt - last_cnt;
        if (keep && delta == 0) {
            return;
        }
        value[0].set(cur_cnt);
        value[1].set(delta);
    }

    @Override
    public long getCount() {
        return value[1].get();
    }
}
