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

package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor;

import io.netty.channel.ChannelHandlerContext;
import qunar.tc.bistoury.remoting.protocol.RequestData;
import qunar.tc.bistoury.remoting.protocol.Datagram;

import java.util.Optional;

/**
 * @author zhenyu.nie created on 2019 2019/5/16 16:23
 */
public interface CommunicateCommandProcessor<T> {

    /**
     * @param requestData
     * @param ctx
     * @return
     */
    Optional<RequestData<T>> preprocessor(RequestData<String> requestData, ChannelHandlerContext ctx) throws Exception;

    /**
     * 对请求进行提前处理，将其处理为agent能够识别的协议格式
     *
     * @param id
     * @param data
     * @param agentId
     * @return
     */
    Datagram prepareRequest(String id, RequestData<T> data, String agentId);

    /**
     * 对请求的响应结果进行提前处理
     *
     * @param datagram
     * @return
     */
    Datagram prepareResponse(Datagram datagram);
}
