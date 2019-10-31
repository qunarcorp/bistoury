package qunar.tc.bistoury.proxy.service;

import com.google.common.base.Splitter;
import com.google.common.util.concurrent.AtomicLongMap;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.remoting.command.ProfilerSearchCommand;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.PayloadHolder;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.RequestPayloadHolder;
import qunar.tc.bistoury.serverside.dao.ProfilerDao;
import qunar.tc.bistoury.serverside.dao.ProfilerDaoImpl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen created on 2019/10/30 16:54
 */
@Service
public class DefaultProfilerStateManager implements ProfilerStateManager {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newScheduledThreadPool(5);

    private final AtomicLongMap<ProfilerDatagram> agentConnRemainTimes = AtomicLongMap.create();

    private static final int defaultAdditionalSeconds = 60;

    private static final Splitter SPACE_SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();

    private final ProfilerDao profilerDao = new ProfilerDaoImpl();

    @Resource
    private AgentConnectionStore agentConnectionStore;

    @PostConstruct
    public void init() {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            for (Map.Entry<ProfilerDatagram, Long> datagramEntry : agentConnRemainTimes.asMap().entrySet()) {
                ProfilerDatagram datagram = datagramEntry.getKey();
                if (datagramEntry.getValue() > 0) {
                    stop(datagram.profilerId);
                    agentConnRemainTimes.remove(datagram);
                    continue;
                }
                agentConnRemainTimes.addAndGet(datagram, 3);
                agentConnectionStore.getConnection(datagram.agentId).ifPresent(agentConn -> {
                    agentConn.write(datagram.datagram);
                });
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    @Override
    public void register(AgentConnection agentConnection, String command, String profilesId) {
        ProfilerDatagram profilerDatagram = new ProfilerDatagram(agentConnection.getAgentId(), profilesId);
        int duration = -getDuration(command) - defaultAdditionalSeconds;
        agentConnRemainTimes.addAndGet(profilerDatagram, duration);
    }

    @Override
    public void stop(String profilesId) {
        profilerDao.stopProfiler(profilesId);
    }

    private int getDuration(String command) {
        Iterable<String> segments = SPACE_SPLITTER.split(command);
        for (Iterator<String> iter = segments.iterator(); iter.hasNext(); ) {
            String segment = iter.next();
            if (segment.equals("-d")) {
                return Integer.parseInt(iter.next());
            }
        }
        return 30;
    }

    private static class ProfilerDatagram {

        private final Datagram datagram;

        private final String profilerId;

        private final String agentId;

        private ProfilerDatagram(String agentId, String profilerId) {
            String content = JacksonSerializer.serialize(new ProfilerSearchCommand(profilerId));
            PayloadHolder payloadHolder = new RequestPayloadHolder(content);
            this.datagram = RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_PROFILER_FILE_SEARCH.getCode(), profilerId, payloadHolder);
            this.profilerId = profilerId;
            this.agentId = agentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProfilerDatagram that = (ProfilerDatagram) o;
            return Objects.equals(datagram, that.datagram) &&
                    Objects.equals(profilerId, that.profilerId) &&
                    Objects.equals(agentId, that.agentId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(datagram, profilerId, agentId);
        }
    }
}

