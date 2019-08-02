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


public enum ValueType {

    VALUE("数值"),
    //
    MIN("最小值"),
    //
    MAX("最大值"),
    //
    MEAN("平均值"),
    //
    STD("方差"),
    //
    P75(""),
    //
    P98(""),
    //
    MEAN_RATE("TPS"),
    //
    MIN_1("1分钟TPS"),
    //
    MIN_5("5分钟TPS"),
    //
    MIN_15("15分钟TPS");

    private final String text;

    ValueType(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}