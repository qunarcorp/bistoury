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


import qunar.tc.bistoury.instrument.client.metrics.Counter;

public class CounterAdapter implements Counter {

    protected final com.codahale.metrics.Counter _counter;

    public CounterAdapter(com.codahale.metrics.Counter counter) {
        this._counter = counter;
    }

    @Override
    public void inc() {
        inc(1);
    }

    @Override
    public void inc(long n) {
        _counter.inc(n);
    }

    @Override
    public void dec() {
        dec(1);
    }

    @Override
    public void dec(long n) {
        _counter.dec(n);
    }

    @Override
    public long getCount() {
        return _counter.getCount();
    }
}