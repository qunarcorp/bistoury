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

package qunar.tc.bistoury.attach.arthas.debug;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.AttachJacksonSerializer;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;

/**
 * @author zhenyu.nie created on 2018 2018/11/23 16:58
 */
@Name(BistouryConstants.REQ_DEBUG_SEARCH)
public class QDebugSearchCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final int SUCCESS = 0;

    private static final int NO_BREAKPOINT = 1;

    private static final int NOT_READY = 2;

    private static final int CONDITION_FAIL = 3;

    private String id;

    @Argument(index = 0, argName = "id")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void process(CommandProcess process) {
        logger.info("receive debug search command, id [" + id + "]");
        CodeProcessResponse<Snapshot> codeResponse = new CodeProcessResponse<>();
        TypeResponse<Snapshot> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_DEBUG_SEARCH);
        typeResponse.setData(codeResponse);
        try {
            QDebugClient client = QDebugClients.getInstance();
            Snapshot snapshot = client.getSnapshot(id);
            codeResponse.setId(id);
            if (snapshot == null) {
                codeResponse.setCode(NO_BREAKPOINT);
            } else if (snapshot.isInit()) {
                codeResponse.setCode(SUCCESS);
                codeResponse.setData(snapshot);
            } else if (snapshot.isFail()) {
                codeResponse.setCode(CONDITION_FAIL);
            } else {
                codeResponse.setCode(NOT_READY);
            }
        } catch (Throwable e) {
            logger.error("qdebug-search-error", e.getMessage(), e);
            codeResponse.setId(id);
            codeResponse.setCode(-1);
            codeResponse.setMessage(e.getMessage());
        }

        process.write(URLCoder.encode(AttachJacksonSerializer.serialize(typeResponse)));
        process.end();
    }
}
