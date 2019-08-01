package qunar.tc.bistoury.attach.file;

import com.google.common.base.Strings;

import java.util.Set;

/**
 * @author leix.xie
 * @date 2019-07-26 11:38
 * @describe
 */
public abstract class AbstractFileService implements FileService {
    public boolean isExclusionFile(final Set<String> exclusionFileSuffix, Set<String> exclusionFile, String fileName) {
        if (Strings.isNullOrEmpty(fileName)) {
            return false;
        }

        if (exclusionFile.contains(fileName)) {
            return true;
        }

        for (String fileSuffix : exclusionFileSuffix) {
            if (fileName.endsWith(fileSuffix)) {
                return true;
            }
        }
        return false;
    }
}
