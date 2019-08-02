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

package qunar.tc.bistoury.remoting.netty;


import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.common.CharsetUtils;

/**
 * @author: leix.xie
 * @date: 2019/1/9 10:56
 * @describe：
 */
public class MonitorReceiver implements ResponseHandler {

    private static final String EMPTY = "";
    private String result = EMPTY;

    public MonitorReceiver() {
    }

    public String getAndReset() {
        String current = result;
        result = EMPTY;
        return current;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void handle(String line) {
        result += line;
    }

    @Override
    public void handle(int code, String line) {
        handle(line);
    }

    @Override
    public void handle(int code, byte[] data) {
        handle(data);
    }

    @Override
    public void handle(byte[] dataBytes) {
        result += CharsetUtils.toUTF8String(dataBytes);
    }

    @Override
    public void handleError(int errorCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleError(String error) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleError(Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleEOF() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleEOF(int exitCode) {
        throw new UnsupportedOperationException();
    }

}
