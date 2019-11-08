package qunar.tc.bistoury.common;

import com.google.common.base.Optional;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author cai.wen created on 2019/11/6 13:11
 */
public class ProfilerUtil {

    public static Optional<File> getProfilerDir(String rootDir, final String profilerId) {
        File root = new File(rootDir);
        File[] children = root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(profilerId);
            }
        });
        if (children == null || children.length == 0) {
            return Optional.absent();
        }
        return Optional.of(children[0]);
    }
}
