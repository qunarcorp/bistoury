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

import com.taobao.arthas.core.shell.ShellServerOptions;
import com.taobao.arthas.core.shell.future.Future;
import com.taobao.arthas.core.shell.handlers.Handler;
import com.taobao.arthas.core.shell.system.impl.QGlobalJobControllerImpl;

import java.lang.reflect.Field;

/**
 * @author zhenyu.nie created on 2018 2018/11/19 19:56
 */
public class ShellServerImpl extends com.taobao.arthas.core.shell.impl.ShellServerImpl {

    private BistouryBootstrap bootstrap;

    public ShellServerImpl(ShellServerOptions options, BistouryBootstrap bootstrap) {
        super(options);
        this.bootstrap = bootstrap;
        try {
            Field jobController = com.taobao.arthas.core.shell.impl.ShellServerImpl.class.getDeclaredField("jobController");
            jobController.setAccessible(true);
            jobController.set(this, new QGlobalJobControllerImpl());
        } catch (Exception e) {
            throw new IllegalStateException("create shell server error");
        }
    }

    @Override
    public void close(Handler<Future<Void>> completionHandler) {
        try {
            super.close(completionHandler);
        } catch (NullPointerException e) {
            // 只有options构造函数的ShellServerImpl貌似会抛npe...
        }
        bootstrap.destroy();
    }
}
