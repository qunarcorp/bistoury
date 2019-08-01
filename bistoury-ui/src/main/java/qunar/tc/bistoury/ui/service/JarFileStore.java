package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.ui.model.MavenInfo;

/**
 * @author zhenyu.nie created on 2019 2019/4/25 19:10
 */
public interface JarFileStore {

    String getJarFile(MavenInfo mavenInfo);

    String getJarFileIfPresent(MavenInfo mavenInfo);
}
