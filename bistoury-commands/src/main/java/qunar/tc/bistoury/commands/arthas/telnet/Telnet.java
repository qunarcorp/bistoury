/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.commands.arthas.telnet;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import org.apache.commons.net.telnet.TelnetClient;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.common.BistouryConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author zhenyu.nie created on 2018 2018/10/15 19:01
 */
public abstract class Telnet {

    protected static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

    protected static final Charset charset = Charsets.UTF_8;

    protected static final String PROMPT = "$";

    protected static final byte PROMPT_BYTE = '$';

    private final TelnetClient client;

    protected final InputStream in;

    private final BufferedWriter out;

    private String version;

    public Telnet(TelnetClient client) throws IOException {
        this.client = client;
        this.in = client.getInputStream();
        this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), charset));
        this.version = readVersionUtilPrompt();
    }

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
                if (str.trim().endsWith(PROMPT)) {
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

    public String getVersion() {
        return version;
    }

    public abstract void read(String command, ResponseHandler responseHandler) throws Exception;

    public void close() {
        try {
            client.disconnect();
        } catch (Exception e) {
            // ignore
        }
    }
}
