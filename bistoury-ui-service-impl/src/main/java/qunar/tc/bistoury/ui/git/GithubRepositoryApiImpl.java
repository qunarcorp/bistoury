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

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.AsyncHttpClientHolder;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.metrics.Metrics;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.model.GitHubFile;
import qunar.tc.bistoury.ui.model.PrivateToken;
import qunar.tc.bistoury.ui.security.LoginContext;
import qunar.tc.bistoury.ui.service.GitPrivateTokenService;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * @author leix.xie
 * @date 2019/9/4 16:51
 * @describe
 */
public class GithubRepositoryApiImpl implements GitRepositoryApi {

    private static final Logger logger = LoggerFactory.getLogger(GithubRepositoryApiImpl.class);

    private GitPrivateTokenService privateTokenService;

    private AsyncHttpClient client = AsyncHttpClientHolder.getInstance();

    private String filePathFormat;

    private String gitEndPoint;

    public GithubRepositoryApiImpl(GitPrivateTokenService privateTokenService, DynamicConfig config) {
        this.privateTokenService = privateTokenService;
        filePathFormat = config.getString("file.path.format", "{0}src/main/java/{1}.java");
        gitEndPoint = config.getString("git.endpoint", "");
    }

    @Override
    public ApiResult file(String projectId, String path, String ref) {
        return doFile(projectId, path, ref);
    }

    @Override
    public ApiResult fileByClass(String projectId, String ref, String module, String className) {
        String filePath = getFilePath(module, className);
        return doFile(projectId, ref, filePath);
    }


    private ApiResult doFile(final String project, final String ref, final String path) {
        Optional<PrivateToken> privateToken = privateTokenService.queryToken(LoginContext.getLoginContext().getLoginUser());
        if (!privateToken.isPresent()) {
            return ResultHelper.fail(-1, "尚未设置 Github Private Token");
        }

        String fileUrl = buildFileUrl(project, path);
        Request request = client.prepareGet(fileUrl)
                .addQueryParam("ref", ref)
                .addHeader("Accept", "application/json")
                .addHeader("'content-type", "application/json")
                .addHeader("Authorization", "token " + privateToken.get().getPrivateToken())
                .build();
        try {
            Response response = client.executeRequest(request).get();
            int statusCode = response.getStatusCode();
            switch (statusCode) {
                case 200:
                    String responseBody = response.getResponseBody(Charsets.UTF_8.name());
                    return ResultHelper.success(JacksonSerializer.deSerialize(responseBody, GitHubFile.class));
                case 401:
                    return ResultHelper.fail("拒绝访问，请检查private token");
                case 404:
                    return ResultHelper.fail("文件不存在，请检查链接：" + fileUrl);
                default:
                    return ResultHelper.fail("请求github失败，位置的状态码：" + statusCode);
            }
        } catch (Exception e) {
            Metrics.counter("connect_github_error").inc();
            logger.error("connect github fail", e);
            return ResultHelper.fail("连接 github 失败" + e.getMessage());
        }
    }

    private String buildFileUrl(final String projectId, final String path) {
        if (Strings.isNullOrEmpty(gitEndPoint)) {
            throw new RuntimeException("git 链接配置错误");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(gitEndPoint)
                .append("/repos/")
                .append(projectId)
                .append("/contents/")
                .append(path);
        return sb.toString();
    }


    private String getFilePath(String module, final String className) {
        if (".".equals(module) || Strings.isNullOrEmpty(module)) {
            module = "";
        } else {
            module = module + "/";
        }
        return MessageFormat.format(filePathFormat, module, className.replace(".", "/"));
    }

    @Override
    public void destroy() {
        client.close();
    }

}
