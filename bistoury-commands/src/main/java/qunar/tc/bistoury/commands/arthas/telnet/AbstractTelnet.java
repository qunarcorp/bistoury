package qunar.tc.bistoury.commands.arthas.telnet;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
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

    private static final int DEFAULT_BUFFER_SIZE = CommunicateUtil.DEFAULT_BUFFER_SIZE;

    private final TelnetClient client;

    private final InputStream in;

    private final BufferedWriter out;

    private final String version;

    public AbstractTelnet(TelnetClient client) throws IOException {
        this.client = client;
        this.in = client.getInputStream();
        this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), Charsets.UTF_8));
        this.version = readVersionUtilPrompt();
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
            if (size != -1) {
                String str = new String(b, 0, size);
                sb.append(str);
                if (str.trim().endsWith(CommunicateUtil.LAST_PROMPT_STR)) {
                    return parseVersion(sb.toString());
                }
            } else {
                throw new IllegalVersionException();
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
    public final void read(String command, ResponseHandler responseHandler) throws Exception {
        Writer writer = new DefaultWriter(responseHandler);
        ResultProcessor resultProcessor = getProcessor(writer);
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

    protected abstract ResultProcessor getProcessor(Writer writer);

    @Override
    public void close() {
        try {
            client.disconnect();
        } catch (Exception e) {
            // ignore
        }
    }
}
