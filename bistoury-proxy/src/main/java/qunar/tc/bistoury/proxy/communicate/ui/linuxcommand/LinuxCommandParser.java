package qunar.tc.bistoury.proxy.communicate.ui.linuxcommand;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;

import java.util.Map;
import java.util.Set;

public class LinuxCommandParser {

    private static final Map<String, StandardCommand> standardCommands = Maps.newHashMap(); // name => options

    static {
        for (StandardCommand command : StandardCommand.values()) {
            standardCommands.put(command.getCommandName(), command);
        }
    }

    /**
     * @param line 待解析命令
     * @return 错误信息
     */
    public static LinuxCommand parse(String line) throws ParseException {
        String[] array = splitIgnoreQuotes(line);
        if (array.length == 0) {
            throw new ParseException("Command can't be empty");
        }
        StandardCommand command = standardCommands.get(array[0]);
        if (command == null) {
            throw new ParseException("Illegal command " + array[0] + ", use " + standardCommands.keySet());
        }
        return new LinuxCommand(command, new DefaultParser().parse(command.getStandardOptions(), array));
    }

    public static boolean isLegalCommand(String command) {
        return standardCommands.containsKey(command);
    }

    public static Set<String> getLegalCommands() {
        return ImmutableSet.copyOf(standardCommands.keySet());
    }

    private static String[] splitIgnoreQuotes(String line) {
        StrTokenizer tokenizer = new StrTokenizer(line, StrMatcher.spaceMatcher());
        tokenizer.setIgnoreEmptyTokens(true);
        tokenizer.setQuoteMatcher(StrMatcher.quoteMatcher());
        return tokenizer.getTokenArray();
    }
}
