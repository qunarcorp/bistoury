package qunar.tc.bistoury.proxy.service.impl;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.proxy.generator.IdGenerator;
import qunar.tc.bistoury.proxy.generator.SessionIdGenerator;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerDataManager;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.RequestPayloadHolder;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static qunar.tc.bistoury.common.BistouryConstants.PROFILER_ROOT_TEMP_PATH;


/**
 * @author cai.wen created on 2019/10/30 10:24
 */
@Service
public class DefaultProfilerDataManager implements ProfilerDataManager {

    private final ScheduledExecutorService stateCollectExecutor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setDaemon(true)
                    .setNameFormat("profiler-file-end-state-collect").build());

    private final Set<String> requestProfilerIds = Sets.newConcurrentHashSet();

    @Resource
    private IdGenerator idGenerator = new SessionIdGenerator();

    @Resource
    private AgentConnectionStore agentConnectionStore;

    private static final int intervalMillis = 100;
    private static final int checkCount = 600;
    private static final int waitMillis = 600 * 100;

    @Override
    public void requestData(String profilerId, String agentId) {
        Optional<AgentConnection> agentConnectionRef = agentConnectionStore.getConnection(agentId);
        if (agentConnectionRef.isPresent() && requestProfilerIds.add(profilerId)) {
            try {
                agentConnectionRef.get().write(initProfilerDatagram(profilerId));
                final ProfileRequestState requestState = new ProfileRequestState(checkCount, profilerId);
                stateCollectExecutor.schedule(() -> scheduleCheckState(requestState), intervalMillis, TimeUnit.MILLISECONDS);
                requestState.waitEndState(waitMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                requestProfilerIds.remove(profilerId);
            }
        }
    }

    private void scheduleCheckState(ProfileRequestState requestState) {
        if (requestState.isEnd() || ProfilerUtil.getProfilerDir(PROFILER_ROOT_TEMP_PATH, requestState.getProfilerId()).isPresent()) {
            requestState.endWrite();
            return;
        }
        requestState.check();
        stateCollectExecutor.schedule(() -> scheduleCheckState(requestState), intervalMillis, TimeUnit.MILLISECONDS);
    }

    private Datagram initProfilerDatagram(String profilerId) {
        return RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_PROFILER_FILE.getCode(), idGenerator.generateId(), new RequestPayloadHolder(profilerId));
    }

    private static class ProfileRequestState {

        private final AtomicInteger checkCount;

        private final String profilerId;

        private final CountDownLatch stateLatch = new CountDownLatch(1);

        private ProfileRequestState(int checkCount, String profilerId) {
            this.checkCount = new AtomicInteger(checkCount);
            this.profilerId = profilerId;
        }

        private void check() {
            checkCount.decrementAndGet();
        }

        private boolean isEnd() {
            return checkCount.get() <= 0;
        }

        private void waitEndState(int waitMillis) throws InterruptedException {
            stateLatch.await(waitMillis, TimeUnit.MILLISECONDS);
        }

        private void endWrite() {
            stateLatch.countDown();
        }

        public String getProfilerId() {
            return profilerId;
        }
    }
}
