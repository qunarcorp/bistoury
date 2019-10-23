package qunar.tc.bistoury.commands.arthas.telnet;

/**
 * @author zhenyu.nie created on 2019 2019/10/12 16:53
 */
public class CommunicateUtil {

    public static final byte[] PROMPT_PREFIX = new byte[] { '[', 'a', 'r', 't', 'h', 'a', 's', '@' };

    public static final byte[] PROMPT_SUFFIX = new byte[] { ']', '$' };

    public static final int MIN_PID_LENGTH = 1;

    public static final int MAX_PID_LENGTH = 5;

    public static final int MIN_PROMPT_LENGTH = PROMPT_PREFIX.length + MIN_PID_LENGTH + PROMPT_SUFFIX.length;

    public static final int MAX_PROMPT_LENGTH = PROMPT_PREFIX.length + MAX_PID_LENGTH + PROMPT_SUFFIX.length;

    public static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

    public static final byte LAST_PROMPT_BYTE = '$';

    public static final String LAST_PROMPT_STR = "$";
}
