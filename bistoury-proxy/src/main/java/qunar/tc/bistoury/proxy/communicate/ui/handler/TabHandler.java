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

package qunar.tc.bistoury.proxy.communicate.ui.handler;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.proxy.communicate.ui.UiResponses;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.CommandSplitter;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.LinuxCommandParser;
import qunar.tc.bistoury.remoting.command.MachineCommand;
import qunar.tc.bistoury.remoting.protocol.*;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.ResponseStringPayloadHolder;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author leix.xie
 * @date 2019/6/13 19:42
 * @describe
 */
public class TabHandler extends ChannelDuplexHandler {
    private static final Splitter SPLITTER = Splitter.on(Pattern.compile("\\s")).trimResults().omitEmptyStrings();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestData<String> data = (RequestData<String>) msg;
        if (data.getType() != CommandCode.REQ_TYPE_TAB.getCode()) {
            ctx.fireChannelRead(msg);
            return;
        }
        try {
            List<CommandSplitter.CommandPart> list = CommandSplitter.split(data.getCommand());
            String lastCommand = list.get(list.size() - 1).getContent();
            List<String> parts = SPLITTER.splitToList(lastCommand);
            char firstChar = parts.get(parts.size() - 1).trim().charAt(0);
            char lastChar = lastCommand.charAt(lastCommand.length() - 1);

            final boolean isLastEmpty = CharMatcher.WHITESPACE.matches(lastChar);
            if (!needAutoComplete(firstChar) && !isLastEmpty) {  // 特殊字符开头的不补全
                ctx.writeAndFlush(UiResponses.createFinishResponse(data));
                return;
            }

            if (parts.size() == 1 && !isLastEmpty) { // 命令补全
                String part = parts.get(0);
                Set<String> legalCommands = LinuxCommandParser.getLegalCommands();
                String ret = "";
                for (String legalCommand : legalCommands) {
                    if (legalCommand.startsWith(part)) {
                        ret += legalCommand + "\n";
                    }
                }
                RemotingHeader requestHeader = new RemotingHeader();
                ctx.writeAndFlush(RemotingBuilder.buildResponseDatagram(ResponseCode.RESP_TYPE_CONTENT.getCode(), requestHeader, new ResponseStringPayloadHolder(ret)));
                ctx.writeAndFlush(RemotingBuilder.buildResponseDatagram(ResponseCode.RESP_TYPE_SINGLE_END.getCode(), requestHeader, null));
                ctx.writeAndFlush(UiResponses.createFinishResponse(data));
            } else {
                MachineCommand command = new MachineCommand();
                data.setType(CommandCode.REQ_TYPE_COMMAND.getCode());
                if (isLastEmpty) { // 执行 ls
                    command.setCommand("ls");
                } else { // 执行 ls 文件名*
                    command.setCommand("ls " + parts.get(parts.size() - 1) + "*");
                }
                data.setCommand(JacksonSerializer.serialize(command));
                ctx.fireChannelRead(data);
            }
        } catch (Throwable t) {
            ctx.writeAndFlush(UiResponses.createProcessRequestErrorResponse(data, t.getMessage()));
        }
    }

    /**
     * 判断是否需要自动补全
     */
    private boolean needAutoComplete(char c) {
        return Character.isDigit(c) || Character.isLetter(c) || c == '.' || c == '/';
    }
}
