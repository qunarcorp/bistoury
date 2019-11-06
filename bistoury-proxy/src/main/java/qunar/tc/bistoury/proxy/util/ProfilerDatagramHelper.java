package qunar.tc.bistoury.proxy.util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.PayloadHolder;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.RequestPayloadHolder;

import java.util.List;

import static qunar.tc.bistoury.common.BistouryConstants.*;
import static qunar.tc.bistoury.common.BistouryConstants.REQ_PROFILER_START_STATE_SEARCH;

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
}
