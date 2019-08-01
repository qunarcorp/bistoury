package qunar.tc.bistoury.commands.arthas.telnet;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;

import java.io.IOException;

/**
 * @author zhenyu.nie created on 2018 2018/11/28 19:06
 */
public class DebugTelnet extends Telnet {

    private static final Logger logger = LoggerFactory.getLogger(DebugTelnet.class);

    public DebugTelnet(TelnetClient client) throws IOException {
        super(client);
    }

    @Override
    public void read(String command, ResponseHandler responseHandler) throws Exception {
        Writer writer = new DefaultWriter(responseHandler);
        ResultProcessor resultProcessor = new SkipFirstLineDecorator(new DataTransferProcessor(writer));
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (true) {
            int size = in.read(buffer);
            if (size == -1) {
                throw new IllegalStateException("read data end, not complete data");
            } else if (size > 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("read data, [{}]", new String(buffer, 0, size, Charsets.UTF_8));
                }
                boolean end = resultProcessor.process(buffer, 0, size);
                if (end) {
                    break;
                }
            }
        }
    }

    private interface Writer {
        void write(byte[] data);
    }

    private static class DefaultWriter implements Writer {

        private final ResponseHandler delegate;

        DefaultWriter(ResponseHandler delegate) {
            this.delegate = delegate;
        }

        public void write(byte[] data) {
            while (true) {
                if (!delegate.isActive()) {
                    logger.warn("send channel is not active");
                    throw new IllegalStateException("send channel is not active");
                } else if (delegate.isWritable()) {
                    delegate.handle(data);
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logger.warn("", e);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    private interface ResultProcessor {

        boolean process(byte[] input, int start, int count);
    }

    private static class SkipFirstLineDecorator implements ResultProcessor {

        private final ResultProcessor delegate;

        private boolean alreadySkip = false;

        private SkipFirstLineDecorator(ResultProcessor delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean process(byte[] input, int start, int count) {
            if (alreadySkip) {
                return delegate.process(input, start, count);
            } else {
                int i = start;
                int end = start + count;
                while (i < end) {
                    if (input[i] == '\n') {
                        alreadySkip = true;
                        if (i + 1 < end) {
                            return delegate.process(input, i + 1, end - i - 1);
                        }
                    }
                    ++i;
                }
                return false;
            }
        }
    }

    private static class DataTransferProcessor implements ResultProcessor {

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

        private final Writer writer;

        DataTransferProcessor(Writer writer) {
            this.writer = writer;
        }

        public boolean process(byte[] input, int start, int count) {
            Preconditions.checkArgument(count > 0 && count <= DEFAULT_BUFFER_SIZE);
            boolean isDataEnd = false;

            int promptIndex = findPrompt(input, start, count);
            if (promptIndex != -1) {
                isDataEnd = true;
                count = promptIndex - start;
            }

            System.arraycopy(input, start, buf, length, count);
            length = length + count;
            decode();
            if (isDataEnd || length > DEFAULT_BUFFER_SIZE) {
                transferData();
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
                    case PROMPT_BYTE:
                        return index;
                    default:
                        return -1;
                }
            }
            return -1;
        }

        private void transferData() {
            byte[] bytes = new byte[decodeLength];
            System.arraycopy(buf, 0, bytes, 0, decodeLength);
            writer.write(bytes);
            if (length > decodeLength) {
                System.arraycopy(buf, decodeLength, buf, 0, length - decodeLength);
            }
            length = length - decodeLength;
            decodeLength = 0;
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
                        if (scanIndex + 2 < length) {
                            int highByte = buf[scanIndex + 1];
                            int lowByte = buf[scanIndex + 2];
                            int b = mapping[highByte] * 16 + mapping[lowByte];
                            buf[fillIndex++] = (byte) b;
                            scanIndex += 3;
                        }
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
}
