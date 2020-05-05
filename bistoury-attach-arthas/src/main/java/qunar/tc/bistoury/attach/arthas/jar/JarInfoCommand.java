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

package qunar.tc.bistoury.attach.arthas.jar;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.AttachJacksonSerializer;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.CodeProcessResponse;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.common.URLCoder;

import java.util.List;

/**
 * @author: leix.xie
 * @date: 2019/2/12 17:43
 * @describe：
 */
@Name(BistouryConstants.REQ_JAR_INFO)
public class JarInfoCommand extends AnnotatedCommand {
    private static final Logger logger = BistouryLoggger.getLogger();

    @Override
    public void process(CommandProcess process) {
        logger.info("receive jar info command");
        CodeProcessResponse<List<String>> response = new CodeProcessResponse<>();
        TypeResponse<List<String>> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_JAR_INFO);
        typeResponse.setData(response);
        try {
            final JarInfoClient client = JarInfoClients.getInstance();
            response.setData(client.jarInfo());
            response.setCode(0);
        } catch (Throwable e) {
            logger.error("jar info error, {}", e.getMessage(), e);
            response.setCode(-1);
            response.setMessage("jar info error: " + e.getMessage());
        } finally {
            process.write(URLCoder.encode(AttachJacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }
}
