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
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/1/10 19:21
 */
public class MetaRefreshTipProcessor implements Processor<String> {

    private static final Logger logger = LoggerFactory.getLogger(MetaRefreshTipProcessor.class);

    private static final byte[] EMPTY_BYTES = new byte[]{};

    public MetaRefreshTipProcessor() {
    }

    @Override
    public List<Integer> types() {
        return ImmutableList.of(CommandCode.REQ_TYPE_REFRESH_TIP.getCode());
    }

    @Override
    public void process(RemotingHeader header, String command, ResponseHandler handler) {
        logger.info("meta refresh receive tip");
        handler.handle(CommandCode.REQ_TYPE_REFRESH_AGENT_INFO.getCode(), EMPTY_BYTES);
    }
}
