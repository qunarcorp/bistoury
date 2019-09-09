package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.serverside.bean.ApiResult;

import java.io.IOException;

/**
 * @author leix.xie
 * @date 2019/9/6 18:48
 * @describe
 */
public interface GitRepositoryStoreService {
    ApiResult file(final String projectId, final String path, final String ref) throws IOException;

    ApiResult fileByClass(final String projectId, final String ref, final String module, final String className) throws IOException;
}
