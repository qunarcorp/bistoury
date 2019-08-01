package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.ui.model.ReleaseInfo;

/**
 * @author leix.xie
 * @date 2019/7/10 10:26
 * @describe
 */
public interface ReleaseInfoService {
    ReleaseInfo parseReleaseInfo(final String content);

    String getDefaultReleaseInfoPath();
}
