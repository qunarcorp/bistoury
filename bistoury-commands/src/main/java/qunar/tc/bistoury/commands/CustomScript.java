package qunar.tc.bistoury.commands;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.File;
import java.util.Map;

/**
 * Created by cai.wen on 18-10-11.
 * 处理自定义脚本
 *
 * @author cai.wen.
 */
public final class CustomScript {

    private static final String BIN_DIR = "bin";

    private static final String SCRIPT_PATH;

    static {
        File jarFile = new File(CustomScript.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File binFile = new File(jarFile.getParentFile().getParentFile(), BIN_DIR);
        SCRIPT_PATH = binFile.getPath() + File.separator;
    }

    private static final CustomScript INSTANT = new CustomScript();

    private final Map<String, String> scriptPathMap;

    private CustomScript() {
        scriptPathMap = ImmutableMap.copyOf(initScriptMap());
    }

    private Map<String, String> initScriptMap() {
        Map<String, String> scriptMap = Maps.newHashMapWithExpectedSize(1);
        scriptMap.put("qjdump", SCRIPT_PATH + "qjdump.sh");
        scriptMap.put("qjtop", SCRIPT_PATH + "qjtop.sh");
        scriptMap.put("qjmap", SCRIPT_PATH + "qjmap.sh");
        scriptMap.put("qjmxcli", SCRIPT_PATH + "qjmxcli.sh");
        return scriptMap;
    }

    public static Map<String, String> scriptPaths() {
        return INSTANT.scriptPathMap;
    }

    public static boolean containsCustomScript(String command) {
        command = command.trim();
        String firstArg = firstArg(command);
        return INSTANT.scriptPathMap.containsKey(firstArg);
    }

    public static String customScriptPath(String command) {
        command = command.trim();
        String firstArg = firstArg(command);
        return INSTANT.scriptPathMap.get(firstArg);
    }

    public static String replaceScriptPath(String command) {
        command = command.trim();
        if (containsCustomScript(command)) {
            String firstArg = firstArg(command);
            return INSTANT.scriptPathMap.get(firstArg) + command.substring(firstArg.length());
        }
        return command;
    }

    private static String firstArg(String command) {
        if (Strings.isNullOrEmpty(command)) {
            return "";
        }
        int position = command.indexOf(' ');
        if (position == -1) {
            return command;
        }
        return command.substring(0, position);
    }
}