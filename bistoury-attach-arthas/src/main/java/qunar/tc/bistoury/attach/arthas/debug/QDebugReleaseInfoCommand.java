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

import java.io.File;

/**
 * @author: leix.xie
 * @date: 2018/11/30 11:59
 * @describe：
 */
@Name(BistouryConstants.REQ_DEBUG_RELEASE_INFO)
public class QDebugReleaseInfoCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private String address;

    private String fileName;

    @Argument(index = 0, argName = "address")
    public void setAddress(String address) {
        this.address = URLCoder.decode(address);
    }

    @Argument(index = 1, argName = "fileName")
    public void setFileName(String fileName) {
        this.fileName = URLCoder.decode(fileName);
    }

    @Override
    public void process(CommandProcess process) {
        CodeProcessResponse<String> codeResponse = new CodeProcessResponse<>();
        TypeResponse<String> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_DEBUG_RELEASE_INFO);
        typeResponse.setData(codeResponse);
        String path = FileUtil.dealPath(address, fileName);
        try {
            File cmFile = new File(path);
            String fileContent = FileUtil.readFile(cmFile);
            codeResponse.setCode(0);
            codeResponse.setData(fileContent);
        } catch (Exception e) {
            logger.error("get branch or tag error, address: {}, file name: {} ", "qdebugcm", (Object) path, fileName, e);
            codeResponse.setCode(-1);
            codeResponse.setMessage("获取项目信息失败，请检查主机上是否存在" + path + "文件");
        } finally {
            process.write(URLCoder.encode(AttachJacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }
}
