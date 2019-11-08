package qunar.tc.bistoury.proxy.util;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.ProfilerUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cai.wen created on 2019/10/25 16:55
 */
public class ProfilerAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilerAnalyzer.class);

    private static ProfilerAnalyzer INSTANCE = new ProfilerAnalyzer();

    private static String flameGraphFile = new File(Resources.getResource("script/flamegraph.pl").getPath())
            .getAbsolutePath();

    private static final String perlPath = System.getProperty("perl.path");

    private static final String rootName = "bistoury-profiler";

    private static final Joiner COMMANDS_JOINER = Joiner.on(" | ").skipNulls();

    private static final String preAnalyzePath = createTempPath(rootName + File.separator + "tmp");

    private static final String analyzePath = createTempPath(rootName);

    private ProfilerAnalyzer() {
    }

    static {
        createTempPath(rootName);
    }

    public String analyze(final String profilerId) {
        String commands = COMMANDS_JOINER.join(getAllPreAnalyzeCommand(profilerId));
        try {
            if (isLinux()) {
                Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", commands}, null).waitFor();
            } else if (isWindows()) {
                Runtime.getRuntime().exec("cmd /c" + commands).waitFor();
            }
        } catch (Exception e) {
            LOGGER.error("profiler analyze error. id: {}", profilerId, e);
            throw new RuntimeException("analyze profiler id error, id: " + profilerId, e);
        }

        return renameProfilerDir(profilerId);
    }

    private String renameProfilerDir(String profilerId) {
        File svgParent = ProfilerUtil.getProfilerDir(preAnalyzePath, profilerId).orNull();
        File analysisDir = new File(analyzePath, svgParent.getName());
        Objects.requireNonNull(svgParent).renameTo(analysisDir);
        return analysisDir.getName();
    }

    private List<String> getAllPreAnalyzeCommand(String profilerId) {
        Stream<Path> allChild;
        try {
            File profilerDir = ProfilerUtil.getProfilerDir(preAnalyzePath, profilerId).orNull();
            allChild = Files.list(Objects.requireNonNull(profilerDir).toPath());
        } catch (IOException e) {
            LOGGER.error("list pre analyze file error.");
            throw new RuntimeException("list pre analyze file error", e);
        }

        return allChild.filter(path -> path.toFile().getName().endsWith(".txt"))
                .map(this::getSinglePerlCommand)
                .collect(Collectors.toList());
    }

    private String getSinglePerlCommand(Path dumpTxt) {
        String parent = dumpTxt.getParent().toString();
        String nameWithoutExtension = com.google.common.io.Files.getNameWithoutExtension(dumpTxt.toFile().getName());
        String svgPath = parent + File.separator + nameWithoutExtension + ".svg";
        String realPerlPath = Strings.isNullOrEmpty(perlPath) ? "perl" : perlPath;
        return realPerlPath + " " + flameGraphFile + " " + dumpTxt.toString() + ">" + svgPath;
    }

    public static ProfilerAnalyzer getInstance() {
        return INSTANCE;
    }

    private static String createTempPath(String dirName) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDir, dirName);
        file.mkdir();
        return file.getAbsolutePath();
    }

    private static boolean isLinux() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().contains("linux");
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().contains("windows");
    }
}
