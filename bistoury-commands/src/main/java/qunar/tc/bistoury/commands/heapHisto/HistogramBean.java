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

package qunar.tc.bistoury.commands.heapHisto;

/**
 * @author: leix.xie
 * @date: 2019/4/1 10:42
 * @describe：
 */
public class HistogramBean {
    private long count;
    private long bytes;
    private String className;

    public HistogramBean() {

    }

    public HistogramBean(long count, long bytes, String className) {
        this.count = count;
        this.bytes = bytes;
        this.className = className;
    }

    public HistogramBean(String count, String bytes, String className) {
        this(Long.valueOf(count), Long.valueOf(bytes), className);
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "HistogramBean{" +
                "count=" + count +
                ", bytes=" + bytes +
                ", className='" + className + '\'' +
                '}';
    }
}
