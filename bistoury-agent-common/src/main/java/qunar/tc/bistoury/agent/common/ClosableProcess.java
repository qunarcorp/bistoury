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

package qunar.tc.bistoury.agent.common;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author zhenyu.nie created on 2019 2019/3/14 17:42
 */
public abstract class ClosableProcess extends Process implements Closeable {

    private final Process delegate;

    private final long id;

    ClosableProcess(Process delegate) {
        this.delegate = delegate;
        this.id = JavaProcesses.register(delegate);
    }

    @Override
    public OutputStream getOutputStream() {
        return delegate.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return delegate.getInputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return delegate.getErrorStream();
    }

    @Override
    public int waitFor() throws InterruptedException {
        return delegate.waitFor();
    }

    @Override
    public int exitValue() {
        return delegate.exitValue();
    }

    @Override
    public void destroy() {
        delegate.destroy();
        JavaProcesses.remove(id);
    }

    public abstract byte[] read() throws Exception;

    @Override
    public void close() {
        destroy();
    }
}
