package qunar.tc.bistoury.ui.controller;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import qunar.tc.bistoury.ui.connection.DownloadWebSocket;
import qunar.tc.bistoury.ui.service.ProxyService;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URI;
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
    public void download(@RequestParam final String agentIp,
                         @RequestParam final String command,
                         @RequestParam("name") final String filename,
                         HttpServletResponse response) {
        if (Strings.isNullOrEmpty(agentIp)) {
            throw new RuntimeException("no agent ip");
        }

        List<String> webSocketUrl = proxyService.getWebSocketUrl(agentIp);

        if (webSocketUrl.isEmpty()) {
            throw new RuntimeException("not find proxy for agent " + agentIp);
        }

        String uriStr = webSocketUrl.get(random.nextInt(webSocketUrl.size()));
        try {
            OutputStream outputStream = response.getOutputStream();
            URI uri = new URI(uriStr);

            final String newCommand = new String(Base64.getDecoder().decode(command), Charsets.UTF_8);
            download(uri, newCommand, filename, outputStream, response);
        } catch (Exception e) {
            logger.error("download fail", e);
        }
    }

    private void download(URI uri, String command, final String filename, final OutputStream outputStream, HttpServletResponse response) throws InterruptedException {
        DownloadWebSocket webSocket = null;
        try {
            CountDownLatch latch = new CountDownLatch(1);
            webSocket = new DownloadWebSocket(uri, latch, outputStream, response, filename, command);
            webSocket.connect();
            latch.await();
        } finally {
            if (webSocket != null) {
                webSocket.close();
            }
        }
    }
}
