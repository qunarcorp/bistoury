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

import qunar.tc.bistoury.instrument.client.location.Location;
import qunar.tc.bistoury.instrument.client.spring.el.Expression;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2018 2018/9/21 14:23
 */
public class Breakpoint {

    private final String id;

    private final Location location;

    private final Expression condition;

    private final AtomicBoolean trigger = new AtomicBoolean(false);

    public Breakpoint(String id, Location location, Expression condition) {
        this.id = id;
        this.location = location;
        this.condition = condition;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public Expression getCondition() {
        return condition;
    }

    public boolean trigger() {
        return trigger.compareAndSet(false, true);
    }
}
