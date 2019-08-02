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

package qunar.tc.bistoury.clientside.common.monitor;


import static qunar.tc.bistoury.clientside.common.monitor.ValueType.*;

/**
 * 指标类别
 */
public enum MetricType {

    GAUGE(VALUE),
    COUNTER(VALUE),
    METER(MEAN_RATE, MIN_1, MIN_5, MIN_15),
    TIMER(MEAN_RATE, MIN_1, MIN_5, MEAN, STD, P75, P98);

    /**
     * 该类型对应的值存储序列
     */
    private final ValueType[] sequence;

    MetricType(ValueType... sequence) {
        this.sequence = sequence;
    }

    public static MetricType codeOf(int code) {
        return values()[code];
    }

    public ValueType[] sequence() {
        return sequence;
    }

    public int code() {
        return ordinal();
    }

    public int indexOf(ValueType valueType) {
        for (int i = 0; i < sequence.length; i++) {
            if (sequence[i] == valueType) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(ValueType type) {
        return indexOf(type) >= 0;
    }
}
