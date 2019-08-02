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

import org.apache.commons.net.telnet.TelnetClient;
import qunar.tc.bistoury.agent.common.ResponseHandler;

import java.io.IOException;

/**
 * @author zhenyu.nie created on 2018 2018/11/28 19:02
 */
public class ArthasTelnet extends Telnet {

    public ArthasTelnet(TelnetClient client) throws IOException {
        super(client);
    }

    @Override
    public void read(String command, ResponseHandler responseHandler) throws Exception {
        Handler handler = new Handler(command, responseHandler);
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (true) {
            int size = in.read(buffer);
            if (size != -1) {
                String data = new String(buffer, 0, size, charset);
                handler.handle(data);
                if (data.trim().endsWith(PROMPT)) {
                    break;
                }
            }
        }
    }

    private static class Handler {

        private final String command;

        private final ResponseHandler delegate;

        private String firstLineStr = "";

        private boolean firstLine = true;

        Handler(String command, ResponseHandler delegate) {
            this.command = command;
            this.delegate = delegate;
        }

        void handle(String data) {
            if (!firstLine) {
                doHandle(data);
                return;
            }

            data = trimPrefix(firstLineStr + data);
            int i = data.indexOf('\n');
            if (i > -1) {
                firstLineStr = data.substring(0, i + 1);
                if (!firstLineStr.trim().equals(command)) {
                    doHandle(data);
                } else {
                    if (data.length() > i + 1) {
                        doHandle(data.substring(i + 1));
                    }
                }
                firstLine = false;
            } else {
                firstLineStr = data;
            }
        }

        private void doHandle(String data) {
            delegate.handle(data);
        }

        private String trimPrefix(String str) {
            int start = str.length();
            for (int i = 0; i < str.length(); ++i) {
                if (str.charAt(i) > ' ') {
                    start = i;
                    break;
                }
            }
            return str.substring(start);
        }
    }
}
