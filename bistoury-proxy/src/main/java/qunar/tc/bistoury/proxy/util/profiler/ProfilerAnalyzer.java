package qunar.tc.bistoury.proxy.util.profiler;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.common.OsUtils;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.common.profiler.compact.CompactClassHelper;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.util.ResultHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static qunar.tc.bistoury.serverside.common.BistouryServerConstants.PROFILER_ROOT_PATH;
import static qunar.tc.bistoury.serverside.common.BistouryServerConstants.PROFILER_ROOT_TEMP_PATH;

/**
 * @author cai.wen created on 2019/10/25 16:55
 */
public class ProfilerAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(ProfilerAnalyzer.class);

    private static final ProfilerAnalyzer INSTANCE = new ProfilerAnalyzer();

    //todo 最好放到bin目录下
    private final String PL_FILE_STORE_PATH = System.getProperty("catalina.base") + File.separator + "pl_script";

    private final File flameGraphForTimeFile = new File(PL_FILE_STORE_PATH + File.separator + "flamegraph-time.pl");

    private final File flameGraphForCountFile = new File(PL_FILE_STORE_PATH + File.separator + "flamegraph-count.pl");

    private static final String perlPath = System.getProperty("perl.path");

    private static final Joiner COMMANDS_JOINER = Joiner.on(" ; ").skipNulls();

    private static final String preAnalyzePath = PROFILER_ROOT_TEMP_PATH;

    private static final String analyzePath = PROFILER_ROOT_PATH;

    private ProfilerAnalyzer() {
        try {
            flameGraphForTimeFile.delete();
            flameGraphForCountFile.delete();
            new File(PL_FILE_STORE_PATH).mkdirs();
            Files.copy(getClass().getResourceAsStream("/script/flamegraph-count.pl"), Paths.get(flameGraphForCountFile.getAbsolutePath()));
            Files.copy(getClass().getResourceAsStream("/script/flamegraph-time.pl"), Paths.get(flameGraphForTimeFile.getAbsolutePath()));
            logger.info("pl info: flamegraph-count: {}, flamegraph-time: {}", flameGraphForCountFile.getAbsolutePath(), flameGraphForTimeFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("copy pl file error.", e);
        }
    }

    public void analyze(final String profilerId, Profiler.Mode mode) {
        String commands = COMMANDS_JOINER.join(getAllPreAnalyzeCommand(profilerId, mode));
        try {
            if (OsUtils.isLinux() || OsUtils.isMac()) {
                new ProcessBuilder()
                        .redirectErrorStream(true)
                        .redirectError(new File("/tmp/bistoury-profiler-error.log"))
                        .redirectOutput(new File("/tmp/bistoury-profiler-out.log"))
                        .command("/bin/sh", "-c", commands)
                        .start()
                        .waitFor();
            } else if (OsUtils.isWindows()) {
                Runtime.getRuntime().exec("cmd /c" + commands).waitFor();
            }
            if (mode == Profiler.Mode.async_sampler) {
                doParseFile(profilerId);
            }
        } catch (Exception e) {
            logger.error("profiler analyze error. id: {}", profilerId, e);
            throw new RuntimeException("analyze profiler id error, id: " + profilerId, e);
        }
    }

    private static final String javaMethodTag = "//";

    private void doParseFile(String profilerId) throws IOException {
        Optional<File> rootDir = ProfilerUtil.getProfilerDir(preAnalyzePath, profilerId);
        if (rootDir.isPresent()) {
            File sourceFile = Objects.requireNonNull(rootDir.get().listFiles((dir, name) -> name.endsWith(".collapsed")))[0];
            File hotMethod = new File(sourceFile.getParent(), hotMethodFile);
            File javaMethod = new File(sourceFile.getParent(), JavaHotMethodFile);
            File javaCompactMethod = new File(sourceFile.getParent(), JavaHotMethodCompactFile);
            parseHotMethod(sourceFile, hotMethod, stack -> stack);
            parseHotMethod(sourceFile, javaMethod, stack -> stack.stream()
                    .filter(this::isJavaMethod)
                    .collect(Collectors.toList()));
            parseHotMethod(sourceFile, javaCompactMethod, this::getCompactStack);
        }
    }

    private List<String> getCompactStack(List<String> stack) {
        List<String> result = new ArrayList<>(stack.size() / 2);
        boolean preClassIsCompact = false;
        for (String info : stack) {
            if (!isJavaMethod(info)) {
                continue;
            }
            String className = info.split(javaMethodTag)[0];
            boolean isCompact = CompactClassHelper.isCompactClass(className);
            if (isCompact && !preClassIsCompact) {
                result.add(info);
            }
            if (!isCompact) {
                result.add(info);
            }
            preClassIsCompact = isCompact;
        }
        return result;
    }

    private boolean isJavaMethod(String method) {
        return method.contains(javaMethodTag);
    }

    public String renameProfilerDir(String profilerId) {
        File svgParent = ProfilerUtil.getProfilerDir(preAnalyzePath, profilerId).orNull();
        File analysisDir = new File(analyzePath, svgParent.getName());
        Objects.requireNonNull(svgParent).renameTo(analysisDir);
        return analysisDir.getName();
    }

    private static final String hotMethodFile = "hotMethod.json";
    private static final String JavaHotMethodFile = "hotMethod-java.json";
    private static final String JavaHotMethodCompactFile = "hotMethod-java-compact.json";

    private void parseHotMethod(File collapsedFile, File targetFile, Function<List<String>, List<String>> methodFilter) throws IOException {
        TreeNode<FunctionCounter> treeNode = HotSpotMethodParser.parse(collapsedFile, methodFilter);
        HotSpotMethodFormatter.DisplayNode root = HotSpotMethodFormatter.format(treeNode);
        String jsonText = JacksonSerializer.serialize(ResultHelper.success(root));
        com.google.common.io.Files.write(jsonText, targetFile, Charsets.UTF_8);
    }

    private List<String> getAllPreAnalyzeCommand(String profilerId, Profiler.Mode mode) {
        Stream<Path> allChild;
        try {
            File profilerDir = ProfilerUtil.getProfilerDir(preAnalyzePath, profilerId).orNull();
            allChild = Files.list(Objects.requireNonNull(profilerDir).toPath());
        } catch (IOException e) {
            logger.error("list pre analyze file error.");
            throw new RuntimeException("list pre analyze file error", e);
        }

        return allChild.filter(path -> path.toFile().getName().endsWith(".collapsed"))
                .map(path -> getSinglePerlCommand(path, mode))
                .collect(Collectors.toList());
    }

    private String getSinglePerlCommand(Path dumpTxt, Profiler.Mode mode) {
        String parent = dumpTxt.getParent().toString();
        String nameWithoutExtension = com.google.common.io.Files.getNameWithoutExtension(dumpTxt.toFile().getName());
        String svgPath = parent + File.separator + nameWithoutExtension + ".svg";
        String realPerlPath = Strings.isNullOrEmpty(perlPath) ? "perl" : perlPath;
        String flameGraphFile = mode == Profiler.Mode.async_sampler ? flameGraphForCountFile.getAbsolutePath() : flameGraphForTimeFile.getAbsolutePath();
        return realPerlPath + " " + flameGraphFile + " --color=java " + dumpTxt.toString() + ">  " + svgPath;
    }

    public static ProfilerAnalyzer getInstance() {
        return INSTANCE;
    }
}
