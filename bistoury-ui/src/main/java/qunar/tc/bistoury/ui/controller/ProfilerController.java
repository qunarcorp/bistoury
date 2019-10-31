package qunar.tc.bistoury.ui.controller;

import com.google.common.io.ByteStreams;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qunar.tc.bistoury.serverside.dao.ProfilerDao;
import qunar.tc.bistoury.serverside.dao.ProfilerDaoImpl;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.service.ProxyService;
import qunar.tc.bistoury.ui.util.ProxyInfo;
import qunar.tc.bistoury.ui.util.ProxyInfoParse;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.Random;


/**
 * @author cai.wen created on 2019/10/28 15:35
 */
@Controller
@RequestMapping("profiler")
public class ProfilerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilerController.class);

    private static final AsyncHttpClient httpClient = new AsyncHttpClient(
            new AsyncHttpClientConfig.Builder().setReadTimeout(30000).build());

    private static final String profilerSvgUrl = "http://%s:%d/proxy/agent/profiler/svg?profilerId=%s&svgName=%s";

    private static final String profilerResultUrl = "http://%s:%d/proxy/agent/profiler/result?profilerId=%s";

    private final ProfilerDao profilerDao = new ProfilerDaoImpl();

    @Resource
    private ProxyService proxyService;

    @PostMapping("/state")
    @ResponseBody
    public Object requestProfilerState(String profilerId) {
        return profilerDao.getProfilerRecord(profilerId);
    }

    @GetMapping("/download")
    public void forwardSvgFile(@RequestParam("profilerId") String profilerId,
                               @RequestParam("svgName") String svgName,
                               HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/svg+xml");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(getSvgFile(new ProxyInfo("127.0.0.1", 9090, 9090), profilerId, svgName));
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    @PostMapping("analyze")
    @ResponseBody
    public Object analyzeProfiler(@RequestParam("profilerId") String profilerId) {
        ProxyInfo proxyInfo = getRandomProxy();
        try {
            String url = String.format(profilerResultUrl, proxyInfo.getIp(), proxyInfo.getTomcatPort(), profilerId);
            getAnalyzerResult(url);
        } catch (Exception e) {
            return ResultHelper.fail("分析失败");
        }
        return ResultHelper.success();
    }

    private void getAnalyzerResult(String url) {
        Request request = httpClient.preparePost(url).build();
        try {
            Response response = httpClient.executeRequest(request).get();
            if (response.getStatusCode() != 200) {
                LOGGER.warn("analyze profiler result code is {}", response.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.error("get analyzer result.", e);
            throw new RuntimeException("get analyzer result error. " + e.getMessage());
        }
    }


    private ProxyInfo getRandomProxy() {
        List<String> proxyWebSocketUrls = proxyService.getAllProxyUrls();
        int randomIndex = new Random().nextInt(proxyWebSocketUrls.size());
        String proxyUrl = proxyWebSocketUrls.get(randomIndex);
        Optional<ProxyInfo> proxyRef = ProxyInfoParse.parseProxyInfo(proxyUrl);
        if (proxyRef.isPresent()) {
            return proxyRef.get();
        }

        for (String proxyWebSocketUrl : proxyWebSocketUrls) {
            proxyRef = ProxyInfoParse.parseProxyInfo(proxyWebSocketUrl);
            if (!proxyRef.isPresent()) {
                continue;
            }
            return proxyRef.get();
        }
        throw new RuntimeException("获取可用的proxy失败");
    }


    private byte[] getSvgFile(ProxyInfo proxyInfo, String profilerId, String svgName) {
        String url = String.format(profilerSvgUrl, proxyInfo.getIp(), proxyInfo.getTomcatPort(), profilerId, svgName);
        return getBytesFromUrl(url);
    }

    private byte[] getBytesFromUrl(String url) {
        Request request = httpClient.preparePost(url).build();
        try {
            Response response = httpClient.executeRequest(request).get();
            return ByteStreams.toByteArray(response.getResponseBodyAsStream());
        } catch (Exception e) {
            LOGGER.error("get byte from proxy error.", e);
            throw new RuntimeException("get svg file error. " + e.getMessage());
        }
    }

}
