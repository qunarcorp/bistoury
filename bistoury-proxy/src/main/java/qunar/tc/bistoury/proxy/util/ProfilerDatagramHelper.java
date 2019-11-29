package qunar.tc.bistoury.proxy.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.PayloadHolder;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.RequestPayloadHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static qunar.tc.bistoury.common.BistouryConstants.*;

/**
 * @author cai.wen created on 2019/11/6 8:50
 */
public class ProfilerDatagramHelper {

    private static final Joiner SPACE_JOINER = Joiner.on(" ").skipNulls();

    public static Datagram createStartStateSearchDatagram(String profilerId) {
        List<String> command = ImmutableList.of(REQ_PROFILER_STATE_SEARCH, profilerId, PID_PARAM + FILL_PID, REQ_PROFILER_START_STATE_SEARCH);
        PayloadHolder holder = new RequestPayloadHolder(SPACE_JOINER.join(command));
        return RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_PROFILER_STATE_SEARCH.getCode(), profilerId, holder);
    }

    public static Datagram createStartDatagram(String profilerId, String command) {
        PayloadHolder holder = new RequestPayloadHolder(command);
        return RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_PROFILER_START.getCode(), profilerId, holder);
    }

    public static Datagram createStopDatagram(String profilerId) {
        List<String> command = ImmutableList.of(REQ_PROFILER_STOP, profilerId, PID_PARAM + FILL_PID);
        PayloadHolder holder = new RequestPayloadHolder(SPACE_JOINER.join(command));
        return RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_PROFILER_STOP.getCode(), profilerId, holder);
    }

    public static Datagram createFinishStateSearchDatagram(String profilerId) {
        List<String> command = ImmutableList.of(REQ_PROFILER_STATE_SEARCH, profilerId, PID_PARAM + FILL_PID, REQ_PROFILER_FINNSH_STATE_SEARCH);
        PayloadHolder searchHolder = new RequestPayloadHolder(SPACE_JOINER.join(command));
        return RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_PROFILER_STATE_SEARCH.getCode(), profilerId, searchHolder);
    }

    private static boolean isProfilerResult(Datagram datagram) {
        return datagram.getHeader().getCode() == -2;
    }

    private static TypeResponse<Map<String, String>> getProfilerResponse(ByteBuf body) {
        byte[] data = new byte[body.readableBytes()];
        body.readBytes(data);
        return JacksonSerializer.deSerialize(data, new TypeReference<TypeResponse<Map<String, String>>>() {
        });
    }

    public static Optional<TypeResponse<Map<String, String>>> getProfilerResponse(Datagram datagram) {
        if (!isProfilerResult(datagram)) {
            return Optional.empty();
        }

        TypeResponse<Map<String, String>> response = getProfilerResponse(datagram.getBody().slice());
        return Optional.of(response);
    }

    public static boolean getResultState(TypeResponse<Map<String, String>> response) {
        Map<String, String> data = response.getData().getData();
        String state = data.get("state");
        return state != null && Boolean.valueOf(state);
    }

    public static String getProfilerId(TypeResponse<Map<String, String>> response) {
        return response.getData().getData().get("profilerId");
    }
}
