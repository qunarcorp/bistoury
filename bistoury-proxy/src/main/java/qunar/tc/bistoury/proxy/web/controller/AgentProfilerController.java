package qunar.tc.bistoury.proxy.web.controller;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.service.ProfilerAnalyzer;
import qunar.tc.bistoury.proxy.service.ProfilerDataManager;
import qunar.tc.bistoury.serverside.util.ResultHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author cai.wen created on 2019/10/25 16:52
 */
@Controller
@RequestMapping("/proxy/agent/profiler")
public class AgentProfilerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentProfilerController.class);

    @RequestMapping("/svg")
    public ResponseEntity<byte[]> download(@RequestParam("profilerId") String profilerId,
                                           @RequestParam("svgName") String svgName,
                                           HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        HttpHeaders headers = new HttpHeaders();
        Path path = getSvgFile(profilerId, svgName);
        headers.setContentDispositionFormData("attachment", path.toString());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(Files.readAllBytes(path), headers, HttpStatus.CREATED);
    }

    @Resource
    private ProfilerDataManager profilerDataManager;

    @RequestMapping("/result")
    @ResponseBody
    public Object result(@RequestParam("profilerId") String profilerId) {
        if (Strings.isNullOrEmpty(profilerId)) {
            return ResultHelper.fail("profiler id is empty.");
        }
        profilerDataManager.requestData(profilerId);
        try {
            ProfilerAnalyzer.getInstance().analyze(profilerId);
        } catch (Exception e) {
            LOGGER.error("analyze result error. profiler id: {}", profilerId);
            throw e;
        }
        return ResultHelper.success();
    }

    private Path getSvgFile(String profilerId, String svgName) {
        return Paths.get(BistouryConstants.PROFILER_ROOT_PATH, profilerId, svgName);
    }
}
