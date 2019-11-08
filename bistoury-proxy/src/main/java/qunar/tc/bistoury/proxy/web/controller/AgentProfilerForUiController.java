package qunar.tc.bistoury.proxy.web.controller;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerDataManager;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerStateManager;
import qunar.tc.bistoury.proxy.util.ProfilerAnalyzer;
import qunar.tc.bistoury.serverside.util.ResultHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import static qunar.tc.bistoury.common.BistouryConstants.PROFILER_ROOT_PATH;

/**
 * @author cai.wen created on 2019/10/25 16:52
 */
@Controller
@RequestMapping("/proxy/profiler")
public class AgentProfilerForUiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentProfilerForUiController.class);

    @RequestMapping("/svg")
    public ResponseEntity<byte[]> download(String profilerId,
                                           String svgName,
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

    @Resource
    private ProfilerStateManager profilerStateManager;

    @RequestMapping("/result")
    @ResponseBody
    public Object result(String profilerId) {
        if (Strings.isNullOrEmpty(profilerId)) {
            return ResultHelper.fail("profiler id is empty.");
        }
        profilerDataManager.requestData(profilerId);
        try {
            return ProfilerAnalyzer.getInstance().analyze(profilerId);
        } catch (Exception e) {
            LOGGER.error("analyze result error. profiler id: {}", profilerId);
            throw e;
        }
    }

    @RequestMapping("/analysis/state")
    @ResponseBody
    public Object getAnalysisState(String profilerId) {
        Optional<File> fileRef = ProfilerUtil.getProfilerDir(BistouryConstants.PROFILER_ROOT_PATH, profilerId);
        Map<String, Object> result = ImmutableMap.of();
        if (fileRef.isPresent()) {
            result = ImmutableMap.of("name", fileRef.get().getName());
        }
        return ResultHelper.success(result);
    }

    @RequestMapping("/searchStopState")
    @ResponseBody
    public Object searchStopState(String profilerId) {
        try {
            profilerStateManager.searchStopState(profilerId);
        } catch (Exception e) {
            LOGGER.error("search stop state error.profiler id: {}", profilerId, e);
            ResultHelper.fail(e.getMessage());
        }
        return ResultHelper.success();
    }

    @RequestMapping("/stop")
    @ResponseBody
    public Object forceStop(String agentId, String profilerId) {
        try {
            profilerStateManager.forceStop(agentId, profilerId);
        } catch (Exception e) {
            LOGGER.error("force stop profiler error.profiler id: {}", profilerId, e);
            ResultHelper.fail(e.getMessage());
        }
        return ResultHelper.success();
    }

    private Path getSvgFile(String profilerId, String svgName) {
        File profilerFile = ProfilerUtil.getProfilerDir(PROFILER_ROOT_PATH, profilerId).orNull();
        return Paths.get(Objects.requireNonNull(profilerFile).getAbsolutePath(), svgName);
    }
}
