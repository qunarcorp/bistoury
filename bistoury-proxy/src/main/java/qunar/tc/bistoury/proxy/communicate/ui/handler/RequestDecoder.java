package qunar.tc.bistoury.proxy.communicate.ui.handler;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.UiResponses;
import qunar.tc.bistoury.proxy.communicate.ui.handler.encryption.RequestEncryption;
import qunar.tc.bistoury.remoting.protocol.CommandCode;

import java.io.IOException;
import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/5/16 15:43
 */
@ChannelHandler.Sharable
public class RequestDecoder extends MessageToMessageDecoder<WebSocketFrame> {

    private final RequestEncryption encryption;

    public RequestDecoder(RequestEncryption encryption) {
        this.encryption = encryption;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        RequestData<String> data = parse(msg);
        if (data != null) {
            //code转换，将老agent的code转换为新agent能识别的code
            Optional<CommandCode> optional = CommandCode.valueOfOldCode(data.getType());
            if (optional.isPresent()) {
                data.setType(optional.get().getCode());
                out.add(data);
            } else {
                ctx.writeAndFlush(UiResponses.createWrongFrameResponse());
            }
        } else {
            ctx.writeAndFlush(UiResponses.createWrongFrameResponse());
        }
    }

    private RequestData<String> parse(WebSocketFrame msg) throws IOException {
        if (msg instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) msg).text();
            return encryption.decrypt(text);
        } else if (msg instanceof BinaryWebSocketFrame) {
            ByteBuf content = msg.content();
            byte[] data = new byte[content.readableBytes()];
            content.readBytes(data);
            return encryption.decrypt(new String(data, Charsets.UTF_8));
        } else {
            return null;
        }
    }
}
