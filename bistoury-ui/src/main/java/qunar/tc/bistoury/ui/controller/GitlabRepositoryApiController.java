package qunar.tc.bistoury.ui.controller;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.GitlabAPIException;
import org.gitlab.api.http.Query;
import org.gitlab.api.models.GitlabProject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.model.GitlabFile;
import qunar.tc.bistoury.ui.service.GitlabApiCreateService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author keli.wang
 */
@Controller
@RequestMapping("/api/gitlab/repository")
public class GitlabRepositoryApiController {

    @Resource
    private GitlabApiCreateService gitlabApiCreateService;

    private String filePathFormat;

    @PostConstruct
    public void init() {
        DynamicConfigLoader.load("config.properties").addListener(config -> filePathFormat = config.getString("file.path.format", "{0}src/main/java/{1}.java"));
    }

    @RequestMapping("/tree")
    @ResponseBody
    public ApiResult tree(@RequestParam final String projectId,
                          @RequestParam final String path,
                          @RequestParam final String ref) throws IOException {
        try {
            final GitlabAPI api = gitlabApiCreateService.create();
            final GitlabProject project = api.getProject(projectId);
            return ResultHelper.success(api.getRepositoryTree(project, path, ref));
        } catch (GitlabAPIException e) {
            return ResultHelper.fail(-1, "连接gitlab服务器失败，请核对private token", e);
        } catch (FileNotFoundException fnfe) {
            return ResultHelper.fail(-1, "文件不存在，请核对仓库地址", fnfe);
        }
    }

    @RequestMapping("/file")
    @ResponseBody
    public ApiResult file(@RequestParam final String projectId,
                          @RequestParam final String ref,
                          @RequestParam final String filepath) throws IOException {
        return doFile(projectId, ref, filepath);
    }

    @RequestMapping("/filebyclass")
    @ResponseBody
    public ApiResult file(@RequestParam final String projectId,
                          @RequestParam final String ref,
                          @RequestParam(required = false) final String module,
                          @RequestParam final String className) throws IOException {
        final String filePath = getFilePath(module, className);
        return doFile(projectId, ref, filePath);
    }

    @RequestMapping("/blobs")
    @ResponseBody
    public String blobs(@RequestParam final String projectId,
                        @RequestParam final String sha,
                        @RequestParam final String filepath) throws IOException {
        final GitlabAPI api = gitlabApiCreateService.create();
        final GitlabProject project = api.getProject(projectId);
        return new String(api.getRawFileContent(project, sha, filepath), Charsets.UTF_8);
    }

    @RequestMapping("/raw_blobs")
    @ResponseBody
    public String rawBlobs(@RequestParam final String projectId,
                           @RequestParam final String sha) throws IOException {
        final GitlabAPI api = gitlabApiCreateService.create();
        final GitlabProject project = api.getProject(projectId);
        return new String(api.getRawBlobContent(project, sha), Charsets.UTF_8);
    }


    private ApiResult doFile(final String projectId, final String ref, final String filepath) throws IOException {
        try {
            final GitlabAPI api = gitlabApiCreateService.create();
            final GitlabProject project = api.getProject(projectId);
            final Query query = new Query().append("file_path", filepath).append("ref", ref);
            final String url = "/projects/" + project.getId() + "/repository/files" + query.toString();
            return ResultHelper.success(api.retrieve().to(url, GitlabFile.class));
        } catch (GitlabAPIException e) {
            return ResultHelper.fail(-1, "连接gitlab服务器失败，请核private token", e);
        } catch (FileNotFoundException fnfe) {
            return ResultHelper.fail(-1, "文件不存在，请核对仓库地址", fnfe);
        }
    }

    private String getFilePath(String module, final String className) {
        if (".".equals(module) || Strings.isNullOrEmpty(module)) {
            module = "";
        } else {
            module = module + "/";
        }
        return MessageFormat.format(filePathFormat, module, className.replace(".", "/"));
    }
}
