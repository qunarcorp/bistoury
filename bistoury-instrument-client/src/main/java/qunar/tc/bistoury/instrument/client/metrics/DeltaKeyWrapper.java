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

public abstract class DeltaKeyWrapper<T> extends KeyWrapper<T> {

    protected boolean delta = false;
    protected boolean keep = false;

    public DeltaKeyWrapper(String name) {
        super(name);
    }

    /**
     * 只记录变化量
     */
    public DeltaKeyWrapper<T> delta() {
        this.delta = true;
        return this;
    }

    /**
     * 当两次检查数据一致时，维持变化量
     */
    public DeltaKeyWrapper<T> keep() {
        this.keep = true;
        return this;
    }
}
