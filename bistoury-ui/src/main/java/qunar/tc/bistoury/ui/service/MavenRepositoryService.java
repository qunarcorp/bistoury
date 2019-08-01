package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.ui.model.MavenInfo;

/**
 * @author: leix.xie
 * @date: 2019/4/3 10:56
 * @describe：
 */
public interface MavenRepositoryService {
    /**
     * 尝试从磁盘获取源文件内容，不存在时抛出SourceFileNotFoundException异常
     *
     * @param mavenInfo
     * @param className
     * @return
     */
    String getSourceFile(MavenInfo mavenInfo, String className);

    /**
     * 从maven私服获取源文件jar包，然后读取文件内容
     *
     * @param mavenInfo
     * @param className
     * @return
     */
    String downSourceFile(MavenInfo mavenInfo, String className);
}
