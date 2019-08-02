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

import qunar.tc.bistoury.instrument.client.metrics.Timer;

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 15-5-7
 * Time: 下午12:50
 */
public class ResettableTimerAdapter implements Timer {
    private final ResettableTimer record;

    public ResettableTimerAdapter(ResettableTimer resettableTimer) {
        this.record = resettableTimer;
    }

    @Override
    public void update(long duration, TimeUnit unit) {
        record.update(duration, unit);
    }

    @Override
    public Context time() {
        return new ResettableTimerContext(record);
    }

    @Override
    public Context time(long startTime) {
        return new ResettableTimerContext(record, startTime);
    }

    class ResettableTimerContext implements Context {

        private final ResettableTimer timer;
        private final long startTime;

        private ResettableTimerContext(ResettableTimer timer, long startTime) {
            this.timer = timer;
            this.startTime = startTime;
        }

        private ResettableTimerContext(ResettableTimer timer) {
            this.timer = timer;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public long directGet() {
            return stop();
        }

        @Override
        public long stop() {
            final long elapsed = System.currentTimeMillis() - startTime;
            timer.update(elapsed, TimeUnit.MILLISECONDS);
            return elapsed;
        }


        public void close() {
            stop();
        }
    }
}
