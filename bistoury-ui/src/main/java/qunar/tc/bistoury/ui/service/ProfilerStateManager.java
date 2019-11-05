package qunar.tc.bistoury.ui.service;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cai.wen created on 2019/11/4 13:17
 */
@Service
public class ProfilerStateManager {

    private static final AsyncHttpClient httpClient = new AsyncHttpClient(
            new AsyncHttpClientConfig.Builder()
                    .setConnectTimeout(3000)
                    .setReadTimeout(30000).build());

    private static final ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(1, new NamedThreadFactory("clear-expired-profiler-record"));

    private static final String profilerStopUrl = "http://%s:%d/proxy/profiler/stop?profilerId=%s";

    @Resource
    private ProfilerService profilerService;

    @Resource
    private ProxyService proxyService;

    private volatile List<ProxyInfo> proxyInfos;

    private volatile long currentTimeMillis;

    @PostConstruct
    public void init() {
        executor.scheduleAtFixedRate(() -> {
                    proxyInfos = getAllProxyInfo();
                    currentTimeMillis = System.currentTimeMillis();
                    profilerService.getProfilersByState(Profiler.State.ready.code)
                            .forEach(profiler -> stop(profiler.getProfilerId(), profiler.getStartTime(), 60));
                },
                60, 60, TimeUnit.SECONDS);

    }

    private List<ProxyInfo> getAllProxyInfo() {
        return proxyService.getAllProxyUrls().stream()
                .map(ProxyInfoParse::parseProxyInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void stop(String profilerId, Timestamp startTime, int durationSeconds) {
        long startMillis = startTime.getTime();
        if (currentTimeMillis - durationSeconds * 1000 > startMillis) {
            proxyInfos.forEach(proxyInfo -> sendForceStopRequests(proxyInfo, profilerId));
        }
    }

    private void sendForceStopRequests(ProxyInfo proxyInfo, String profilerId) {
        String url = String.format(profilerStopUrl, proxyInfo.getIp(), proxyInfo.getTomcatPort(), profilerId);
        Request request = httpClient.preparePost(url).build();
        httpClient.executeRequest(request);
    }
}
