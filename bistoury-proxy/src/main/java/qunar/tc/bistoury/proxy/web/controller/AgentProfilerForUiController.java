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
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerDataManager;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerService;
import qunar.tc.bistoury.proxy.util.profiler.ProfilerAnalyzer;
import qunar.tc.bistoury.serverside.bean.Profiler;
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

    private final ProfilerAnalyzer profilerAnalyzer = ProfilerAnalyzer.getInstance();

    @Resource
    private ProfilerService profilerService;

    @Resource
    private ProfilerDataManager profilerDataManager;

    @RequestMapping("/file")
    public ResponseEntity<byte[]> download(String profilerId,
                                           String name,
                                           HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        HttpHeaders headers = new HttpHeaders();
        Path path = getFile(profilerId, name);
        headers.setContentDispositionFormData("attachment", path.toString());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(Files.readAllBytes(path), headers, HttpStatus.CREATED);
    }

    @RequestMapping("/result")
    @ResponseBody
    public Object result(String profilerId) {
        if (Strings.isNullOrEmpty(profilerId)) {
            return ResultHelper.fail("profiler id is empty.");
        }
        try {
            Profiler profiler = profilerService.getProfilerRecord(profilerId);
            profilerDataManager.requestData(profilerId, profiler.getAgentId());
            profilerAnalyzer.analyze(profilerId, profiler.getMode());

            return profilerAnalyzer.renameProfilerDir(profilerId);
        } catch (Exception e) {
            LOGGER.error("analyze result error. profiler id: {}", profilerId);
            throw e;
        }
    }

    @RequestMapping("/analysis/state")
    @ResponseBody
    public Object getAnalysisState(String profilerId) {
        Optional<File> fileRef = ProfilerUtil.getProfilerDir(PROFILER_ROOT_PATH, profilerId);
        Map<String, String> result = ImmutableMap.of();
        if (fileRef.isPresent()) {
            result = ImmutableMap.of("name", fileRef.get().getName());
        }
        return ResultHelper.success(result);
    }

    private Path getFile(String profilerId, String svgName) {
        File profilerFile = ProfilerUtil.getProfilerDir(PROFILER_ROOT_PATH, profilerId).orNull();
        return Paths.get(Objects.requireNonNull(profilerFile).getAbsolutePath(), svgName);
    }
}
