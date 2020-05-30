package qunar.tc.bistoury.agent;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.FileUtil;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author leix.xie
 * @date 2019/12/19 17:13
 * @describe
 */
public class DumpFileCleaner {

    private static final Logger logger = LoggerFactory.getLogger(DumpFileCleaner.class);

    private static final String BASE_DUMP_DIR = BistouryStore.getDumpFileStorePath();

    private static final MetaStore META_STORE = MetaStores.getMetaStore();

    private static final long VALIDITY_TIME = TimeUnit.HOURS.toMillis(META_STORE.getLongProperty("dump.file.validity.hour", 1));

    private static final String TIME_PATTERN = "yyyyMMddHHmmssSSS";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern(TIME_PATTERN);

    private final AtomicBoolean init = new AtomicBoolean(false);

    private static final DumpFileCleaner INSTANCE = new DumpFileCleaner();

    static DumpFileCleaner getInstance() {
        return INSTANCE;
    }

    private DumpFileCleaner() {

    }

    public void start() {
        if (!init.compareAndSet(false, true)) {
            return;
        }

        initDumpDir("jstack");
        initDumpDir("qjdump");
        initDumpDir("bistoury-class-dump");
        final File file = new File(BASE_DUMP_DIR);
        ListeningScheduledExecutorService listeningDecorator = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("clean-dump-file")));
        listeningDecorator.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                clean(file);
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    private void initDumpDir(final String dir) {
        String dirPath = FileUtil.dealPath(BASE_DUMP_DIR, dir);
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public void clean(final File file) {
        List<File> files = FileUtil.listFile(file, new Predicate<File>() {
            @Override
            public boolean apply(File input) {
                return isExpire(input);
            }
        });
        int success = 0;
        for (File tmp : files) {
            if (tmp.delete()) {
                success++;
            } else {
                logger.warn("delete file fail, {}", tmp.getAbsolutePath());
            }
        }
        if (success > 0) {
            logger.info("finish clean expire dump file, base path: {}, count: {}, success count: {}", BASE_DUMP_DIR, files.size(), success);
        }
    }

    public boolean isExpire(File file) {
        long createTime = getFileCreateTime(file);
        return createTime + VALIDITY_TIME < System.currentTimeMillis();
    }

    private long getFileCreateTime(File file) {
        try {
            String fileName = file.getName();
            if (fileName.endsWith(".class")) {
                return file.lastModified();
            } else {
                return getFileCreateTime(fileName);
            }
        } catch (Exception e) {
            logger.warn("get file time error, {}", file.getAbsolutePath());
            return file.lastModified();
        }
    }

    private long getFileCreateTime(String fileName) {
        int endIndex = fileName.lastIndexOf(".");
        if (endIndex < 0) {
            endIndex = fileName.length();
        }

        int startIndex = endIndex - TIME_PATTERN.length();
        if (startIndex < 0) {
            throw new RuntimeException();
        }

        String timeStr = fileName.substring(startIndex, endIndex);
        return TIME_FORMATTER.parseDateTime(timeStr).getMillis();
    }
}
