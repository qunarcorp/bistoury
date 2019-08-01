package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor;

import io.netty.channel.ChannelHandlerContext;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
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
    Optional<RequestData<T>> preprocessor(RequestData<String> requestData, ChannelHandlerContext ctx);

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
