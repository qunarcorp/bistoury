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

package qunar.tc.bistoury.remoting.netty;

import io.netty.util.AttributeKey;

/**
 * @author sen.chai
 * @date 15-6-15
 */
public class AgentConstants {

    public static final String CHANNEL_REQUEST_ID_KEY = "CHANNEL_REQUEST_ID";

    /**
     * 存储requeset的id
     */
    public static final AttributeKey<String> attributeKey = AttributeKey.valueOf(CHANNEL_REQUEST_ID_KEY);

    public static final int VERSION = 10;
}

    