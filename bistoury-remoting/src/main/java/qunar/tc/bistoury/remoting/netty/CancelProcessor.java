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

import com.google.common.collect.ImmutableList;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;

/**
 * @author zhenyu.nie created on 2018 2018/10/9 14:31
 */
public class CancelProcessor implements Processor<String> {

    private final TaskStore taskStore;

    public CancelProcessor(TaskStore taskStore) {
        this.taskStore = taskStore;
    }

    @Override
    public List<Integer> types() {
        return ImmutableList.of(CommandCode.REQ_TYPE_CANCEL.getCode());
    }

    @Override
    public void process(RemotingHeader header, String command, ResponseHandler handler) {
        taskStore.cancel(command);
    }
}
