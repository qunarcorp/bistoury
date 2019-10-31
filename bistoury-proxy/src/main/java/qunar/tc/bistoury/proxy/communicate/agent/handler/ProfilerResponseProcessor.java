package qunar.tc.bistoury.proxy.communicate.agent.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.CodeProcessResponse;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.serverside.dao.ProfilerDao;
import qunar.tc.bistoury.serverside.dao.ProfilerDaoImpl;

import java.util.Set;

/**
 * @author cai.wen created on 2019/10/31 10:32
 */
@Service
public class ProfilerResponseProcessor implements AgentMessageProcessor {

    private final ProfilerDao profilerDao = new ProfilerDaoImpl();

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_PROFILER_FILE_SEARCH.getCode());
    }

    @Override
    public void process(ChannelHandlerContext ctx, Datagram message) {
        String profilerId = message.getHeader().getId();
        CodeProcessResponse<String> response = getTypeResponse(message.getBody());
        if (Boolean.parseBoolean(response.getData())) {
            profilerDao.stopProfiler(profilerId);
        }
    }

    private CodeProcessResponse<String> getTypeResponse(ByteBuf body) {
        byte[] data = new byte[body.readableBytes()];
        body.readBytes(data);
        String response = new String(data, Charsets.UTF_8);
        return JacksonSerializer.deSerialize(response, new TypeReference<CodeProcessResponse<String>>() {
        });
    }
}
