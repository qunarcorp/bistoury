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

package qunar.tc.bistoury.instrument.client.debugger;

/**
 * @author keli.wang
 */
final class LocalVariable {
    private final String name;
    private final String desc;
    private final int start;
    private final int end;
    private final int index;

    LocalVariable(final String name,
                  final String desc,
                  final int start,
                  final int end, final int index) {
        this.name = name;
        this.desc = desc;
        this.start = start;
        this.end = end;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "LocalVariable{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", index=" + index +
                '}';
    }
}
