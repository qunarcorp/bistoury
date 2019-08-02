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

package qunar.tc.bistoury.instrument.client.monitor;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

/**
 * @author: leix.xie
 * @date: 2018/12/28 14:16
 * @describe：
 */
public interface Monitor {
    boolean startup(InstrumentInfo instrumentInfo);

    String addMonitor(final String source, final int line);

    void removeMonitor(final String source, final int line, String monitorId);

    void destroy();
}
