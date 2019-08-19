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

package qunar.tc.bistoury.agent.common;

import com.google.common.base.Joiner;
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


    public static final Joiner COMMA_JOINER = Joiner.on(",").skipNulls();

    //////// agent shared meta store key
    public static final String AGENT_SERVER_CONFIG_INFO = "_AGENT_SERVER_CONFIG_INFO";

    public static final String SUPPORT_GET_PID_FROM_PROXY = "_SUPPORT_GET_PID_FROM_PROXY";

    public static final String AGENT_SERVER_PID_INFO = "_AGENT_SERVER_PID_INFO";

    public static final String APP_CODES_DEPLOY_ON_AGENT_SERVER_COMMA_SPLIT = "_APP_CODES_DEPLOY_ON_AGENT_SERVER_COMMA_SPLIT";


    /////// app level meta store key
    public static final String APP_CODE = "_APP_CODE";

    public static final String PID = "_PID";

    public static final String TELNET_CONNECT_PORT = "_TELNET_CONNECT_PORT";

}

    