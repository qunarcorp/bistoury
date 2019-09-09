/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.ui.git;

import com.google.common.base.Strings;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.GitlabAPIException;
import org.gitlab.api.http.Query;
import org.gitlab.api.models.GitlabProject;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.metrics.Metrics;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.model.GitlabFile;
import qunar.tc.bistoury.ui.model.PrivateToken;
import qunar.tc.bistoury.ui.security.LoginContext;
import qunar.tc.bistoury.ui.service.GitPrivateTokenService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

/**
 * @author leix.xie
 * @date 2019/9/4 16:37
 * @describe
 */
public class GitlabRepositoryApiImpl implements GitRepositoryApi {

    private GitPrivateTokenService gitPrivateTokenService;

    private String filePathFormat;

    private String gitEndPoint;

    public GitlabRepositoryApiImpl(GitPrivateTokenService privateTokenService, DynamicConfig config) {
        this.gitPrivateTokenService = privateTokenService;
        filePathFormat = config.getString("file.path.format", "{0}src/main/java/{1}.java");
        gitEndPoint = config.getString("git.endpoint");
    }

    @Override
    public ApiResult file(String projectId, String path, String ref) throws IOException {
        return doFile(projectId, path, ref);
    }

    @Override
    public ApiResult fileByClass(String projectId, String ref, String module, String className) throws IOException {
        final String filePath = getFilePath(module, className);
        return doFile(projectId, ref, filePath);
    }

    private ApiResult doFile(final String projectId, final String ref, final String filepath) throws IOException {
        try {
            final GitlabAPI api = createGitlabApi();
            final GitlabProject project = api.getProject(projectId);
            final Query query = new Query().append("file_path", filepath).append("ref", ref);
            final String url = "/projects/" + project.getId() + "/repository/files" + query.toString();
            return ResultHelper.success(api.retrieve().to(url, GitlabFile.class));
        } catch (GitlabAPIException e) {
            Metrics.counter("connect_gitlab_error").inc();
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


    private GitlabAPI createGitlabApi() {
        String userCode = LoginContext.getLoginContext().getLoginUser();
        Optional<PrivateToken> token = gitPrivateTokenService.queryToken(userCode);
        if (!token.isPresent()) {
            throw new RuntimeException("尚未设置 Git Private Token");
        }
        return GitlabAPI.connect(gitEndPoint, token.get().getPrivateToken());
    }

    @Override
    public void destroy() {

    }
}
