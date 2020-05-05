package qunar.tc.bistoury.commands.arthas.telnet;

import static qunar.tc.bistoury.commands.arthas.telnet.CommunicateUtil.*;

import com.google.common.base.Preconditions;

/**
 * @author zhenyu.nie created on 2019 2019/10/14 13:47
 */
class UrlDecodeProcessor implements ResultProcessor {

    private static final int[] mapping = getMapping();

    private static int[] getMapping() {
        int[] mapping = new int[128];
        for (int ch = '0'; ch <= '9'; ++ch) {
            mapping[ch] = ch - '0';
        }

        for (int ch = 'a'; ch <= 'f'; ++ch) {
            mapping[ch] = ch - 'a' + 10;
        }

        for (int ch = 'A'; ch <= 'F'; ++ch) {
            mapping[ch] = ch - 'A' + 10;
        }
        return mapping;
    }

    private final byte[] buf = new byte[DEFAULT_BUFFER_SIZE * 2];

    private int decodeLength = 0;

    private int length = 0;

    private final ResultProcessor delegate;

    UrlDecodeProcessor(ResultProcessor delegate) {
        this.delegate = delegate;
    }

    public boolean process(byte[] input, int start, int count) {
        Preconditions.checkArgument(count > 0 && count <= DEFAULT_BUFFER_SIZE);
        boolean isDataEnd = false;

        int promptIndex = findPrompt(input, start, count);
        if (promptIndex != -1) {
            isDataEnd = true;
            count = promptIndex - start + 1;
        }

        System.arraycopy(input, start, buf, length, count);
        length = length + count;
        decode();
        if (isDataEnd || length > DEFAULT_BUFFER_SIZE) {
            boolean delegateEnd = transferData();

        }
        return isDataEnd;
    }

    private int findPrompt(byte[] input, int start, int count) {
        int index = start + count - 1;
        while (index >= start) {
            switch (input[index]) {
                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    index--;
                    break;
                case LAST_PROMPT_BYTE:
                    return index;
                default:
                    return -1;
            }
        }
        return -1;
    }

    private boolean transferData() {
        boolean end = delegate.process(buf, 0, decodeLength);
        if (length > decodeLength) {
            System.arraycopy(buf, decodeLength, buf, 0, length - decodeLength);
        }
        length = length - decodeLength;
        decodeLength = 0;
        return end;
    }

    private void decode() {
        int scanIndex = decodeLength;
        int fillIndex = scanIndex;
        while (scanIndex < length) {
            if (scanIndex + 2 >= length && buf[scanIndex] == '%') {
                break;
            }

            switch (buf[scanIndex]) {
                case '+':
                    buf[fillIndex++] = ' ';
                    scanIndex++;
                    break;
                case '%':
                    int highByte = buf[scanIndex + 1];
                    int lowByte = buf[scanIndex + 2];
                    int b = mapping[highByte] * 16 + mapping[lowByte];
                    buf[fillIndex++] = (byte) b;
                    scanIndex += 3;
                    break;
                default:
                    buf[fillIndex++] = buf[scanIndex++];
                    break;
            }
        }
        this.decodeLength = fillIndex;
        while (scanIndex < length) {
            buf[fillIndex++] = buf[scanIndex++];
        }
        this.length = fillIndex;
    }
}
