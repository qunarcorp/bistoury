package qunar.tc.bistoury.ui.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.service.ProfilerService;
import qunar.tc.bistoury.ui.service.ProxyService;
import qunar.tc.bistoury.ui.util.ProxyInfo;
import qunar.tc.bistoury.ui.util.ProxyInfoParse;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author cai.wen created on 2019/10/28 15:35
 */
@Controller
@RequestMapping("profiler")
public class ProfilerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilerController.class);

    private static final AsyncHttpClient httpClient = new AsyncHttpClient(
            new AsyncHttpClientConfig.Builder()
                    .setConnectTimeout(3000)
                    .setReadTimeout(1000 * 60 * 3).build());

    private static final String profilerFileUrl = "http://%s:%d/proxy/profiler/file?profilerId=%s&name=%s";

    private static final String profilerResultUrl = "http://%s:%d/proxy/profiler/result?profilerId=%s";

    private static final String profilerIsAnalyzedUrl = "http://%s:%d/proxy/profiler/analysis/state?profilerId=%s";

    private static final String profilerStartUrl = "http://%s:%d/proxy/profiler/start?appCode=%s&agentId=%s&duration=%s";

    @Resource
    private ProxyService proxyService;

    @Resource
    private ProfilerService profilerService;

    @GetMapping("/get")
    @ResponseBody
    public Object requestProfiler(String profilerId) {
        return ResultHelper.success(profilerService.getRecord(profilerId));
    }

    @GetMapping("/analysis/info")
    @ResponseBody
    public Object analyzeProfilerState(String profilerId) {
        Optional<ProfilerInfoVo> proxyRef = analyze(profilerId);
        return ResultHelper.success(proxyRef.orElse(null));
    }

    @GetMapping("/records")
    @ResponseBody
    public Object lastThreeDaysProfiler(String appCode, String agentId) {
        List<Profiler> profilers = profilerService.getLastRecords(appCode, agentId, LocalDateTime.now().minusHours(3 * 24));
        profilers = profilers.stream()
                .filter(profiler -> profiler.getState() == Profiler.State.stop)
                .collect(Collectors.toList());
        return ResultHelper.success(profilers);
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/last")
    @ResponseBody
    public Object lastProfiler(String appCode, String agentId) {
        Optional<Profiler> profiler_ref = profilerService.getLastProfilerRecord(appCode, agentId);
        return profiler_ref.map(profiler ->
                ResultHelper.success(ImmutableMap.of("info", profiler, "curTime",
                        LocalDateTime.now().format(DATE_TIME_FORMATTER))))
                .orElseGet(ResultHelper::success);
    }

    @PostMapping("/start")
    @ResponseBody
    public Object start(String appCode, String agentId, String lastProfilerId, long duration) {
        try {
            checkLastProfiler(appCode, agentId, lastProfilerId);
            List<String> proxyWebSocketUrls = proxyService.getAllProxyUrls();
            for (String proxyWebSocketUrl : proxyWebSocketUrls) {
                Optional<ProxyInfo> proxyRef = ProxyInfoParse.parseProxyInfo(proxyWebSocketUrl);
                if (!proxyRef.isPresent()) {
                    continue;
                }
                Optional<String> profilerIdRef = doStartProfiler(proxyRef.get(), appCode, agentId, duration);
                if (profilerIdRef.isPresent()) {
                    return ResultHelper.success(profilerIdRef.get());
                }
            }
        } catch (Exception e) {
            return ResultHelper.fail(e.getMessage());
        }
        return ResultHelper.fail("start profiler error.");
    }

    private void checkLastProfiler(String appCode, String agentId, String lastProfilerId) {
        Optional<Profiler> profiler_ref = profilerService.getLastProfilerRecord(appCode, agentId);
        if (profiler_ref.isPresent()) {
            if (lastProfilerId == null || (!lastProfilerId.equals(profiler_ref.get().getProfilerId()))) {
                throw new RuntimeException("性能分析状态已经发生了更新,请刷新界面,查看最新的状态.");
            }
        }
    }

    private Optional<String> doStartProfiler(ProxyInfo proxyInfo, String appCode, String agentId, long duration) {
        String url = String.format(profilerStartUrl, proxyInfo.getIp(), proxyInfo.getTomcatPort(), appCode, agentId, String.valueOf(duration));
        byte[] content = getBytesFromUrl(url);
        ApiResult<String> apiResult = JacksonSerializer.deSerialize(content, new TypeReference<ApiResult<String>>() {
        });
        if (apiResult.getStatus() == 0) {
            return Optional.of(apiResult.getData());
        }
        return Optional.empty();
    }

    private final TypeReference analyzerResponse = new TypeReference<ApiResult<Map<String, String>>>() {
    };

    private Optional<ProfilerInfoVo> analyze(String profilerId) {
        Optional<ProfilerInfoVo> profilerFileVoRef = getAnalyzedProxyForProfiler(profilerId);
        if (!profilerFileVoRef.isPresent()) {
            return doAnalyze(profilerId);
        }
        return profilerFileVoRef;
    }

    private Optional<ProfilerInfoVo> doAnalyze(String profilerId) {
        ProxyInfo proxyInfo = getProxyForAgent();
        try {
            String url = String.format(profilerResultUrl, proxyInfo.getIp(), proxyInfo.getTomcatPort(), profilerId);
            String profilerFileName = getAnalyzerResult(url);
            Objects.requireNonNull(profilerFileName);
            return Optional.of(new ProfilerInfoVo(proxyInfo, profilerFileName, profilerService.getRecord(profilerId)));
        } catch (Exception e) {
            LOGGER.error("analyze profiler result error. profiler id: {}", profilerId, e);
            return Optional.empty();
        }
    }

    private Optional<ProfilerInfoVo> getAnalyzedProxyForProfiler(String profilerId) {
        List<String> proxyWebSocketUrls = proxyService.getAllProxyUrls();
        for (String proxyWebSocketUrl : proxyWebSocketUrls) {
            Optional<ProxyInfo> proxyRef = ProxyInfoParse.parseProxyInfo(proxyWebSocketUrl);
            if (!proxyRef.isPresent()) {
                continue;
            }
            String name = doGetName(proxyRef.get(), profilerId);
            if (name != null) {
                Profiler profiler = profilerService.getRecord(profilerId);
                return Optional.of(new ProfilerInfoVo(proxyRef.get(), name, profiler));
            }
        }
        return Optional.empty();
    }

    private String doGetName(ProxyInfo proxyInfo, String profilerId) {
        String url = String.format(profilerIsAnalyzedUrl, proxyInfo.getIp(), proxyInfo.getTomcatPort(), profilerId);
        byte[] content = getBytesFromUrl(url);
        ApiResult<Map<String, String>> response = JacksonSerializer.deSerialize(content, analyzerResponse);
        return response.getData().get("name");
    }

    private static final Splitter COLON_SPLITTER = Splitter.on(":");

    @GetMapping("/download")
    public void forwardSvgFile(@RequestParam("profilerId") String profilerId,
                               @RequestParam("name") String name,
                               @RequestParam("proxyUrl") String proxyUrl,
                               @RequestParam(value = "contentType", required = false) String contentType,
                               HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        contentType = Strings.isNullOrEmpty(contentType) ? "image/svg+xml" : contentType;
        response.setContentType(contentType);
        try (ServletOutputStream responseOutputStream = response.getOutputStream()) {
            List<String> info = COLON_SPLITTER.splitToList(proxyUrl);
            String proxyIp = info.get(0);
            int tomcatPort = Integer.parseInt(info.get(1));
            copyFileStream(new ProxyInfo(proxyIp, tomcatPort, 0), profilerId, name, responseOutputStream);
            responseOutputStream.flush();
        }
    }

    private String getAnalyzerResult(String url) {
        Request request = httpClient.preparePost(url).build();
        try {
            Response response = httpClient.executeRequest(request).get();
            if (response.getStatusCode() != 200) {
                LOGGER.warn("analyze profiler result code is {}", response.getStatusCode());
                return null;
            }
            return response.getResponseBody();
        } catch (Exception e) {
            LOGGER.error("get analyzer result.", e);
            throw new RuntimeException("get analyzer result error. " + e.getMessage());
        }
    }


    private ProxyInfo getProxyForAgent() {
        List<String> proxyWebSocketUrls = proxyService.getAllProxyUrls();

        for (String proxyWebSocketUrl : proxyWebSocketUrls) {
            Optional<ProxyInfo> proxyRef = ProxyInfoParse.parseProxyInfo(proxyWebSocketUrl);
            if (!proxyRef.isPresent()) {
                continue;
            }
            return proxyRef.get();
        }
        throw new RuntimeException("获取可用的proxy失败");
    }


    private void copyFileStream(ProxyInfo proxyInfo, String profilerId, String name, ServletOutputStream responseOutputStream) {
        String url = String.format(profilerFileUrl, proxyInfo.getIp(), proxyInfo.getTomcatPort(), profilerId, name);
        Request request = httpClient.preparePost(url).build();
        try {
            Response response = httpClient.executeRequest(request).get();
            ByteStreams.copy(response.getResponseBodyAsStream(), responseOutputStream);
        } catch (Exception e) {
            LOGGER.error("get file from proxy error.", e);
            throw new RuntimeException("get content error. " + e.getMessage());
        }
    }

    private byte[] getBytesFromUrl(String url) {
        Request request = httpClient.preparePost(url).build();
        try {
            Response response = httpClient.executeRequest(request).get();
            return response.getResponseBodyAsBytes();
        } catch (Exception e) {
            LOGGER.error("get byte from proxy error.", e);
            throw new RuntimeException("get content error. " + e.getMessage());
        }
    }

    private static class ProfilerInfoVo {

        private ProxyInfo proxyInfo;
        private int realDuration;
        private Profiler profiler;
        private String eventType;

        public ProfilerInfoVo() {

        }

        public ProfilerInfoVo(ProxyInfo proxyInfo, String name, Profiler profiler) {
            this.proxyInfo = proxyInfo;
            this.realDuration = Integer.parseInt(name.split("-")[1]);
            if (profiler.getMode() == Profiler.Mode.async_sampler) {
                eventType = name.split("-")[2];
            }
            this.profiler = profiler;
        }

        public String getEventType() {
            return eventType;
        }

        public ProxyInfo getProxyInfo() {
            return proxyInfo;
        }

        public int getRealDuration() {
            return realDuration;
        }

        public Profiler getProfiler() {
            return profiler;
        }
    }
}
