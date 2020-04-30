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

package qunar.tc.bistoury.attach.arthas.server;

import com.taobao.arthas.core.advisor.Enhancer;
import com.taobao.arthas.core.shell.ShellServer;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.arthas.core.util.affect.EnhancerAffect;
import com.taobao.arthas.core.util.matcher.WildcardMatcher;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Summary;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClientStore;

import java.lang.instrument.Instrumentation;

/**
 * @author zhenyu.nie created on 2018 2018/11/30 19:52
 */
@Name("shutdown")
@Summary("Shut down bistoury server and exit the console")
public class QShutdownCommand extends AnnotatedCommand {

    @Override
    public void process(CommandProcess process) {
        shutdown(process);
    }

    public static void shutdown(CommandProcess process) {
        try {
            try {
                // 退出之前需要重置所有的增强类
                Instrumentation inst = process.session().getInstrumentation();
                EnhancerAffect enhancerAffect = Enhancer.reset(inst, new WildcardMatcher("*"));
                process.write(enhancerAffect.toString()).write("\n");
            } catch (Exception e) {
                //ignore
            }

            InstrumentClientStore.destroy();

            process.write("Bistoury Server is going to shut down...\n");
        } finally {
            process.end();
            ShellServer server = process.session().getServer();
            server.close();
        }
    }

}
