package qunar.tc.bistoury.attach.file;

import qunar.tc.bistoury.attach.file.bean.FileBean;

import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * @author leix.xie
 * @date 2019-07-25 19:55
 * @describe
 */
public interface FileService {

    String replaceJarWithUnPackDir(String url);

    String readFile(URL url);

    List<FileBean> listFiles(URL url);

    List<FileBean> listFiles(Set<String> exclusionFileSuffix, Set<String> exclusionFile, URL url);
}
