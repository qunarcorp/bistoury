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

package qunar.tc.bistoury.proxy.communicate.ui.command;

import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.CommunicateCommandProcessor;

/**
 * @author zhenyu.nie created on 2019 2019/5/16 16:23
 */
public class CommunicateCommand {

    private final int code;

    private final int minAgentVersion;

    private final boolean supportMulti;

    private final boolean supportPause;

    private final CommunicateCommandProcessor<?> processor;

    public CommunicateCommand(int code,
                              int minAgentVersion,
                              boolean supportMulti,
                              boolean supportPause,
                              CommunicateCommandProcessor processor) {
        this.code = code;
        this.minAgentVersion = minAgentVersion;
        this.supportMulti = supportMulti;
        this.supportPause = supportPause;
        this.processor = processor;
    }

    public int getCode() {
        return code;
    }

    public int getMinAgentVersion() {
        return minAgentVersion;
    }

    public boolean isSupportMulti() {
        return supportMulti;
    }

    public boolean isSupportPause() {
        return supportPause;
    }

    public CommunicateCommandProcessor<?> getProcessor() {
        return processor;
    }
}
