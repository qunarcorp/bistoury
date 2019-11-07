package qunar.tc.bistoury.ui.service;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.ui.util.ProxyInfo;
import qunar.tc.bistoury.ui.util.ProxyInfoParse;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cai.wen created on 2019/11/4 13:17
 */
@Service
public class UiProfilerStateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(UiProfilerStateManager.class);

    private static final AsyncHttpClient httpClient = new AsyncHttpClient(
            new AsyncHttpClientConfig.Builder()
                    .setConnectTimeout(3000)
                    .setReadTimeout(30000).build());

    private static final ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(1, new NamedThreadFactory("clear-expired-profiler-record"));

    private static final String profilerForceStopUrl = "http://%s:%d/proxy/profiler/stop?profilerId=%s&agentId=%s";

    private static final String profilerStopStateSearchUrl = "http://%s:%d/proxy/profiler/searchStopState?profilerId=%s";

    @Resource
    private ProfilerService profilerService;

    @Resource
    private ProxyService proxyService;

    private volatile List<ProxyInfo> proxyInfos;

    private volatile long currentTimeMillis;

    @PostConstruct
    public void init() {
        Random random = new Random(60);
        executor.scheduleAtFixedRate(() -> {
            proxyInfos = getAllProxyInfo();
            currentTimeMillis = System.currentTimeMillis();
            profilerService.getRecordsByState(Profiler.State.ready, 1)
                    .forEach(profiler -> stop(profiler.getProfilerId(), profiler.getStartTime(), 60));
            profilerService.getRecordsByState(Profiler.State.start, 1)
                    .forEach(profiler -> searchStopState(profiler.getProfilerId(), profiler.getStartTime(), profiler.getDuration() + 60));
        }, random.nextInt(61), 60, TimeUnit.SECONDS);

    }

    private void searchStopState(String profilerId, Timestamp startTime, int durationSeconds) {
        try {
            long startMillis = startTime.getTime();
            if (currentTimeMillis - durationSeconds * 1000 > startMillis) {
                proxyInfos.forEach(proxyInfo -> sendStopStateSearchRequests(proxyInfo, profilerId));
            }
        } catch (Exception e) {
            LOGGER.error("stop profiler error. profilerId: {}", profilerId, e);
        }
    }

    private List<ProxyInfo> getAllProxyInfo() {
        return proxyService.getAllProxyUrls().stream()
                .map(ProxyInfoParse::parseProxyInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void stop(String profilerId, Timestamp startTime, int durationSeconds) {
        try {
            long startMillis = startTime.getTime();
            if (currentTimeMillis - durationSeconds * 1000 > startMillis) {
                proxyInfos.forEach(proxyInfo -> sendForceStopRequests(proxyInfo, profilerId));
            }
        } catch (Exception e) {
            LOGGER.error("stop profiler error. profilerId: {}", profilerId, e);
        }
    }

    private void sendForceStopRequests(ProxyInfo proxyInfo, String profilerId) {
        String agentId = profilerService.getRecord(profilerId).getAgentId();
        String url = String.format(profilerForceStopUrl, proxyInfo.getIp(), proxyInfo.getTomcatPort(), profilerId, agentId);
        Request request = httpClient.preparePost(url).build();
        httpClient.executeRequest(request);
    }

    private void sendStopStateSearchRequests(ProxyInfo proxyInfo, String profilerId) {
        String url = String.format(profilerStopStateSearchUrl, proxyInfo.getIp(), proxyInfo.getTomcatPort(), profilerId);
        Request request = httpClient.preparePost(url).build();
        httpClient.executeRequest(request);
    }
}
