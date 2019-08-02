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

package qunar.tc.bistoury.agent.common.pid.impl;

/**
 * @author: leix.xie
 * @date: 2019/3/13 17:27
 * @describe：
 */
public class Priority {

    public static final int FROM_SYSTEM_PROPERTY_PRIORITY = Integer.MIN_VALUE;

    public static final int FROM_JPS_PRIORITY = 10000;

    public static final int FROM_PS_PRIORITY = 20000;
}
