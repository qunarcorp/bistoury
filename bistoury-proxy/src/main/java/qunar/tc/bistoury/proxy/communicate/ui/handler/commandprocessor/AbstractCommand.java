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

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.proxy.communicate.ui.command.UiRequestCommand;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.RequestData;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.RequestPayloadHolder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author zhenyu.nie created on 2019 2019/5/22 11:54
 */
public abstract class AbstractCommand<T> implements CommunicateCommandProcessor<T>, UiRequestCommand {

    private final Class<T> type;

    @SuppressWarnings("all")
    public AbstractCommand() {
        Type superClass = this.getClass().getGenericSuperclass();
        Preconditions.checkArgument(!(superClass instanceof Class) && superClass instanceof ParameterizedType && ((ParameterizedType) superClass).getRawType() == AbstractCommand.class, "[%s]必须是[%s]的子类并且确定了泛型参数");
        ParameterizedType t = (ParameterizedType) superClass;
        Type theType = t.getActualTypeArguments()[0];
        Preconditions.checkArgument(theType instanceof Class && ((Class) theType).getTypeParameters().length == 0, "[%s]参数必须是个非泛型类", theType);
        this.type = (Class<T>) theType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Optional<RequestData<T>> preprocessor(RequestData<String> requestData, ChannelHandlerContext ctx) throws Exception {
        if (type.equals(String.class)) {
            return doPreprocessor((RequestData<T>) requestData, ctx);
        } else {
            T command = JacksonSerializer.deSerialize(requestData.getCommand(), this.type);
            RequestData<T> data = RequestData.copyWithCommand(requestData, command);
            return doPreprocessor(data, ctx);
        }
    }

    protected Optional<RequestData<T>> doPreprocessor(RequestData<T> requestData, ChannelHandlerContext ctx) throws Exception {
        return Optional.of(requestData);
    }

    @Override
    public final Datagram prepareRequest(String id, RequestData<T> data, String agentId) {
        T command = prepareCommand(data, agentId);
        return RemotingBuilder.buildRequestDatagram(data.getType(), id, new RequestPayloadHolder(command));
    }

    protected T prepareCommand(RequestData<T> data, String agentId) {
        return data.getCommand();
    }

    @Override
    public Datagram prepareResponse(Datagram datagram) {
        return datagram;
    }

    @Override
    public boolean supportPause() {
        return true;
    }

    @Override
    public final CommunicateCommandProcessor getProcessor() {
        return this;
    }
}
