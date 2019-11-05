package qunar.tc.bistoury.ui.controller;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.connection.DownloadWebSocket;
import qunar.tc.bistoury.ui.service.ProxyService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author leix.xie
 * @date 2019/11/5 10:42
 * @describe
 */
@Controller
@RequestMapping("file")
public class DownloadFileController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadFileController.class);

    private static final Random random = new Random(System.currentTimeMillis());

    @Autowired
    private ProxyService proxyService;


    @RequestMapping("download")
    public ApiResult download(@RequestParam final String agentIp,
                              @RequestParam final String command,
                              @RequestParam("name") final String filename,
                              HttpServletResponse response) {
        if (Strings.isNullOrEmpty(agentIp)) {
            return ResultHelper.fail("no agent ip");
        }

        List<String> webSocketUrl = proxyService.getWebSocketUrl(agentIp);

        if (webSocketUrl.isEmpty()) {
            return ResultHelper.fail("not find proxy for agent " + agentIp);
        }

        String uriStr = webSocketUrl.get(random.nextInt(webSocketUrl.size()));
        try {
            URI uri = new URI(uriStr);

            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + filename);

            final String newCommand = new String(Base64.getDecoder().decode(command), Charsets.UTF_8);
            download(uri, newCommand, response);

        } catch (URISyntaxException e) {
            return ResultHelper.fail("URI error, " + uriStr);
        } catch (Exception e) {
            return ResultHelper.fail("download fail");
        }

        return ResultHelper.success("下载成功");
    }

    private void download(URI uri, String command, final HttpServletResponse response) throws IOException, InterruptedException {
        DownloadWebSocket webSocket = null;
        try {
            CountDownLatch latch = new CountDownLatch(1);
            webSocket = new DownloadWebSocket(uri, latch, response.getOutputStream(), command);
            webSocket.connect();
            latch.await();
        } finally {
            if (webSocket != null) {
                webSocket.close();
            }
        }
    }
}
