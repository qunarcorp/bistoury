package qunar.tc.bistoury.commands.arthas.telnet;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.BistouryConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/10/14 14:37
 */
public abstract class AbstractTelnet implements Telnet {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTelnet.class);

    private static final byte[] ZERO_BYTES = new byte[0];

    private static final int DEFAULT_BUFFER_SIZE = CommunicateUtil.DEFAULT_BUFFER_SIZE;

    private final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

    private final TelnetClient client;

    private final InputStream in;

    private final BufferedWriter out;

    private final String version;

    private final ResultProcessor resultProcessor;

    private final SettedWriter writer;

    private boolean isEnd = false;

    public AbstractTelnet(TelnetClient client) throws IOException {
        this.client = client;
        this.in = client.getInputStream();
        this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), Charsets.UTF_8));
        this.version = readVersionUtilPrompt();
        this.writer = new SettedWriter();
        this.resultProcessor = getProcessor(writer);
    }

    @Override
    public void write(String command) throws Exception {
        if (command.getBytes(Charsets.UTF_8).length > 999) {
            throw new RuntimeException("the command length is too long，the max length is 999 bytes");
        }
        out.write(command);
        out.newLine();
        out.flush();
    }

    private String readVersionUtilPrompt() throws IOException {
        byte[] b = new byte[DEFAULT_BUFFER_SIZE];
        StringBuilder sb = new StringBuilder();
        while (true) {
            int size = in.read(b);
            if (size == -1) {
                throw new IllegalVersionException();
            } else if (size == 0) {
                // continue
            } else {
                String str = new String(b, 0, size);
                if (logger.isDebugEnabled()) {
                    logger.debug("read data, [{}]", str);
                }
                sb.append(str);
                if (str.trim().endsWith(CommunicateUtil.LAST_PROMPT_STR)) {
                    return parseVersion(sb.toString());
                }
            }
        }
    }

    private String parseVersion(String str) {
        List<String> lines = ImmutableList.of();
        try {
            lines = CharSource.wrap(str).readLines();
        } catch (IOException e) {
            // not happen
        }

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith(BistouryConstants.BISTOURY_VERSION_LINE_PREFIX)) {
                return line.substring(BistouryConstants.BISTOURY_VERSION_LINE_PREFIX.length()).trim();
            }
        }
        return "";
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public byte[] read() throws Exception {
        if (isEnd) {
            return null;
        }

        int size = in.read(buffer);
        if (size == -1) {
            throw new IllegalStateException("read data end, not complete data");
        } else if (size == 0) {
            return ZERO_BYTES;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("read data, [{}]", new String(buffer, 0, size, Charsets.UTF_8));
            }
            isEnd = resultProcessor.process(buffer, 0, size);
            return writer.getAndReset();
        }
    }

    protected abstract ResultProcessor getProcessor(Writer writer);

    @Override
    public void close() {
        try {
            client.disconnect();
        } catch (Exception e) {
            // ignore
        }
    }

    private static class SettedWriter implements Writer {

        private byte[] data = ZERO_BYTES;

        @Override
        public void write(byte[] input) {
            if (data.length == 0) {
                data = input;
                return;
            }

            if (input.length > 0) {
                byte[] newData = new byte[data.length + input.length];
                System.arraycopy(data, 0, newData, 0, data.length);
                System.arraycopy(input, 0, newData, data.length, input.length);
                data = newData;
            }
        }

        public byte[] getAndReset() {
            byte[] result = this.data;
            this.data = ZERO_BYTES;
            return result;
        }
    }
}
