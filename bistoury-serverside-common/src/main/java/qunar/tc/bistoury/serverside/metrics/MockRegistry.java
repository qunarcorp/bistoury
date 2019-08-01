/*
 * Copyright 2018 Qunar, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qunar.tc.bistoury.serverside.metrics;

import com.google.common.base.Supplier;

import java.util.concurrent.TimeUnit;

class MockRegistry implements BistouryMetricRegistry {

    private static final BistouryCounter COUNTER = new MockCounter();

    private static final BistouryMeter METER = new MockMeter();

    private static final BistouryTimer TIMER = new MockTimer();

    @Override
    public void newGauge(String name, String[] tags, String[] values, Supplier<Double> supplier) {

    }

    @Override
    public BistouryCounter newCounter(String name, String[] tags, String[] values) {
        return COUNTER;
    }

    @Override
    public BistouryMeter newMeter(String name, String[] tags, String[] values) {
        return METER;
    }

    @Override
    public BistouryTimer newTimer(String name, String[] tags, String[] values) {
        return TIMER;
    }

    @Override
    public void remove(String name, String[] tags, String[] values) {

    }

    private static class MockCounter implements BistouryCounter {

        @Override
        public void inc() {

        }

        @Override
        public void inc(long n) {

        }

        @Override
        public void dec() {

        }

        @Override
        public void dec(long n) {

        }
    }

    private static class MockMeter implements BistouryMeter {

        @Override
        public void mark() {

        }

        @Override
        public void mark(long n) {

        }
    }

    private static class MockTimer implements BistouryTimer {

        @Override
        public void update(long duration, TimeUnit unit) {

        }
    }
}
