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

import qunar.tc.bistoury.agent.common.pid.PidHandler;

/**
 * @author leix.xie
 * @date 2019/7/11 20:19
 * @describe
 */
public class PidBySystemPropertyHandler extends AbstractPidHandler implements PidHandler {

    @Override
    public int priority() {
        return Priority.FROM_SYSTEM_PROPERTY_PRIORITY;
    }

    @Override
    protected int doGetPid() {
        return Integer.valueOf(System.getProperty("bistoury.user.pid", "-1"));
    }

}
