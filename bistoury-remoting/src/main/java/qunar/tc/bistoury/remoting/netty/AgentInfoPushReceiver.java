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

/**
 * @author leix.xie
 * @date 2019/7/1 16:33
 * @describe
 */
public class AgentInfoPushReceiver implements ResponseHandler {
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

    }

    @Override
    public void handle(int code, String line) {

    }

    @Override
    public void handle(int code, byte[] data) {

    }

    @Override
    public void handle(byte[] dataBytes) {

    }

    @Override
    public void handleError(int errorCode) {

    }

    @Override
    public void handleError(String error) {

    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void handleEOF() {

    }

    @Override
    public void handleEOF(int exitCode) {

    }
}
