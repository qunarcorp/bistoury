package qunar.tc.bistoury.agent.task.profiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;

import java.io.File;

/**
 * @author cai.wen created on 19-11-28 下午5:25
 */
public class TaskRunner implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    private static final int EXPIRE_HOURS = 3 * 24;

    @Override
    public void run() {
        File profilerDir = new File(BistouryStore.DEFAULT_PROFILER_ROOT_PATH);
        File profilerTempDir = new File(BistouryStore.DEFAULT_PROFILER_TEMP_PATH);
        deleteChildrenIfExpired(profilerDir);
        deleteChildrenIfExpired(profilerTempDir);
    }

    private void deleteChildrenIfExpired(File rootDir) {
        if (rootDir.exists()) {
            File[] children = rootDir.listFiles();
            if (children == null) {
                return;
            }
            for (File child : children) {
                deleteIfExpired(child);
            }
        }
    }

    private void deleteIfExpired(File dir) {
        long modifiedTime = dir.lastModified();
        long currentTime = System.currentTimeMillis();
        long diffHours = (currentTime - modifiedTime) / (60 * 60 * 1000);
        if (diffHours > EXPIRE_HOURS) {
            File[] children = dir.listFiles();
            children = children == null ? new File[0] : children;
            boolean deleteState = true;
            for (File child : children) {
                deleteState = deleteState && child.delete();
            }
            deleteState = deleteState && dir.delete();
            if (!deleteState) {
                logger.warn("delete profiler file error. file:{} modifiedTime: {}, currentTime: {}", dir, modifiedTime, currentTime);
            }
        }
    }
}
