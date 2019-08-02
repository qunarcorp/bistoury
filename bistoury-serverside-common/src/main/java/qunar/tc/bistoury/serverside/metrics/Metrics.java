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

package qunar.tc.bistoury.serverside.metrics;

import com.google.common.base.Supplier;

import java.util.ServiceLoader;

/**
 * @author leix.xie
 * @date 2019/7/8 15:59
 * @describe
 */
public class Metrics {
    private static final String[] EMPTY = new String[0];
    private static final BistouryMetricRegistry INSTANCE;

    static {
        ServiceLoader<BistouryMetricRegistry> registries = ServiceLoader.load(BistouryMetricRegistry.class);
        BistouryMetricRegistry instance = null;
        for (BistouryMetricRegistry registry : registries) {
            instance = registry;
        }
        if (instance == null) {
            instance = new MockRegistry();
        }
        INSTANCE = instance;
    }

    public static void gauge(String name, String[] tags, String[] values, Supplier<Double> supplier) {
        INSTANCE.newGauge(name, tags, values, supplier);
    }

    public static void gauge(String name, Supplier<Double> supplier) {
        INSTANCE.newGauge(name, EMPTY, EMPTY, supplier);
    }

    public static BistouryCounter counter(String name, String[] tags, String[] values) {
        return INSTANCE.newCounter(name, tags, values);
    }

    public static BistouryCounter counter(String name) {
        return INSTANCE.newCounter(name, EMPTY, EMPTY);
    }

    public static BistouryMeter meter(String name, String[] tags, String[] values) {
        return INSTANCE.newMeter(name, tags, values);
    }

    public static BistouryMeter meter(String name) {
        return INSTANCE.newMeter(name, EMPTY, EMPTY);
    }

    public static BistouryTimer timer(String name, String[] tags, String[] values) {
        return INSTANCE.newTimer(name, tags, values);
    }

    public static BistouryTimer timer(String name) {
        return INSTANCE.newTimer(name, EMPTY, EMPTY);
    }

    public static void remove(String name, String[] tags, String[] values) {
        INSTANCE.remove(name, tags, values);
    }
}
