package qunar.tc.bistoury.proxy.service;

import com.google.common.base.Splitter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.remoting.command.ProfilerSearchCommand;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.PayloadHolder;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.RequestPayloadHolder;
import qunar.tc.bistoury.serverside.bean.Profiler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cai.wen created on 2019/10/30 16:54
 */
@Service
public class DefaultProfilerStateManager implements ProfilerStateManager {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newScheduledThreadPool(5);

    private final Map<String, ProfilerDatagramHolder> profilingDatagrams = Maps.newConcurrentMap();

    private final Map<String, ProfilerDatagramHolder> readyDatagrams = Maps.newConcurrentMap();

    private static final int defaultAdditionalSeconds = 60;

    private static final Splitter SPACE_SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();

    @Resource
    private ProfilerService profilerService;

    private final Object obj = new Object();

    private final Cache<String, Object> profilerIdCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private static final int delay = 3;

    @Resource
    private AgentConnectionStore agentConnectionStore;

    @PostConstruct
    public void init() {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(this::waitStopState, 0, delay, TimeUnit.SECONDS);
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(this::waitStartState, 0, delay, TimeUnit.SECONDS);
    }

    private void waitStopState() {
        changeState(Profiler.State.start, Profiler.State.start_exception, readyDatagrams);
    }

    private void waitStartState() {
        changeState(Profiler.State.stop, Profiler.State.stop_exception, profilingDatagrams);
    }

    private void changeState(Profiler.State normalState, Profiler.State exceptionalState, Map<String, ProfilerDatagramHolder> datagramHolderMap) {
        List<String> needRemoveIds = new ArrayList<>();
        for (Map.Entry<String, ProfilerDatagramHolder> datagramEntry : datagramHolderMap.entrySet()) {
            String profilerId = datagramEntry.getKey();
            ProfilerDatagramHolder datagramHolder = datagramEntry.getValue();
            if (datagramHolder.isExpired()) {
                needRemoveIds.add(profilerId);
                profilerService.changeState(profilerId, normalState);
                continue;
            }
            datagramHolder.decreaseTime(delay);
            agentConnectionStore.getConnection(datagramHolder.agentId)
                    .ifPresent(agentConn -> agentConn.write(datagramHolder.datagram));
        }
        for (String profilerId : needRemoveIds) {
            profilerService.changeState(profilerId, exceptionalState);
            datagramHolderMap.remove(profilerId);
            forceStop(profilerId);
        }
    }

    @Override
    public String register(String agentId, String command) {
        int profilerDuration = getDuration(command);
        String profilerId = profilerService.prepareProfiler(agentId, profilerDuration);
        Optional<AgentConnection> agentConnRef = agentConnectionStore.getConnection(agentId);
        if (!agentConnRef.isPresent()) {
            throw new RuntimeException("no connection for profiler id. profilerId: " + profilerId);
        }

        profilerIdCache.put(profilerId, obj);

        int readyDuration = 60;
        ProfilerDatagramHolder readyHolder = createStateSearchHolder(agentId, profilerId, readyDuration);
        readyDatagrams.put(profilerId, readyHolder);
        return profilerId;
    }

    @Override
    public boolean isProfilerRequest(String id) {
        return profilerIdCache.getIfPresent(id) != null;
    }

    @Override
    public void dealProfiler(String profilesId, TypeResponse<String> response) {
        String type = response.getType();
        if (type == null) {
            return;
        }
        if (BistouryConstants.REQ_PROFILER_STATE_SEARCH.equals(type)) {
            if (Boolean.parseBoolean(response.getData().getData())) {
                readyDatagrams.remove(profilesId);
                profilerService.startProfiler(profilesId);

                Profiler profiler = profilerService.getProfilerRecord(profilesId);
                int profilingDuration = profiler.getDuration() + defaultAdditionalSeconds;
                ProfilerDatagramHolder profilingHolder = createStopStateSearchDatagram(profiler.getAgentId(), profilesId, profilingDuration);
                profilingDatagrams.put(profiler.getProfilerId(), profilingHolder);
            }
        } else if (BistouryConstants.REQ_PROFILER_FINISH_STATE_SEARCH.equals(type)) {
            if (Boolean.parseBoolean(response.getData().getData())) {
                profilingDatagrams.remove(profilesId);
                profilerService.stopProfiler(profilesId);
            }
        }
    }

    @Override
    public void startProfiler(String profilesId) {

    }

    @Override
    public void forceStop(String profilerId) {
        Profiler profiler = profilerService.getProfilerRecord(profilerId);
        profilerService.changeState(profilerId, Profiler.State.stop_exception);

        String agentId = profiler.getAgentId();
        Datagram datagram = createStateSearchDatagram(profilerId);
        agentConnectionStore.getConnection(agentId)
                .ifPresent(agentConn -> agentConn.write(datagram));
    }

    private ProfilerDatagramHolder createStopStateSearchDatagram(String agentId, String profilerId, int duration) {
        String content = JacksonSerializer.serialize(new ProfilerSearchCommand(profilerId));
        PayloadHolder searchHolder = new RequestPayloadHolder(content);
        Datagram dumpDatagram = RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_PROFILER_FINISH_STATE_SEARCH.getCode(), profilerId, searchHolder);
        return new ProfilerDatagramHolder(agentId, profilerId, dumpDatagram, duration);
    }

    private ProfilerDatagramHolder createStateSearchHolder(String agentId, String profilerId, int duration) {
        Datagram searchDatagram = createStateSearchDatagram(profilerId);
        return new ProfilerDatagramHolder(agentId, profilerId, searchDatagram, duration);
    }

    private Datagram createStateSearchDatagram(String profilerId) {
        PayloadHolder holder = new RequestPayloadHolder(
                BistouryConstants.REQ_PROFILER_STATE_SEARCH + " " + profilerId + "" + BistouryConstants.PID_PARAM + BistouryConstants.FILL_PID);
        return RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_PROFILER_STATE_SEARCH.getCode(), profilerId, holder);
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

    private static class ProfilerDatagramHolder {

        private final AtomicInteger time;

        private final Datagram datagram;

        private final String profilerId;

        private final String agentId;

        private ProfilerDatagramHolder(String agentId, String profilerId, Datagram datagram, int duration) {
            this.datagram = datagram;
            this.profilerId = profilerId;
            this.agentId = agentId;
            this.time = new AtomicInteger(-duration);
        }

        private void decreaseTime(int value) {
            time.addAndGet(value);
        }

        private boolean isExpired() {
            return time.get() > 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProfilerDatagramHolder that = (ProfilerDatagramHolder) o;
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

