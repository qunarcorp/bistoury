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

package qunar.tc.bistoury.commands;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.netty.Processor;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

import java.util.List;

/**
 * @author zhenyu.nie created on 2018 2018/10/29 19:05
 */
public class HeartbeatProcessor implements Processor<String> {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatProcessor.class);

    @Override
    public List<Integer> types() {
        return ImmutableList.of(ResponseCode.RESP_TYPE_HEARTBEAT.getCode());
    }

    @Override
    public void process(RemotingHeader header, String command, ResponseHandler handler) {
        if (logger.isDebugEnabled()) {
            logger.debug("receive heartbeat response, {}", header.getId());
        }
    }
}
