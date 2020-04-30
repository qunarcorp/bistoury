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

import com.google.common.util.concurrent.RateLimiter;
import qunar.tc.bistoury.common.Throwables;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author zhenyu.nie created on 2019 2019/7/16 17:01
 */
public class UnixProcess extends ClosableProcess {

    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final int BUF_SIZE = 4 * 1024;

    private final byte[] buffer = new byte[BUF_SIZE];

    private final RateLimiter rateLimiter = RateLimiter.create(16); //限制每秒read的次数

    private final Process delegate;

    private final Field hasExited;

    UnixProcess(Process delegate) {
        super(delegate);
        this.delegate = delegate;
        try {
            hasExited = delegate.getClass().getDeclaredField("hasExited");
            hasExited.setAccessible(true);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * 与jdk8中isAlive效果一样，用于小于8的jdk
     *
     * @return
     */
    private boolean isProcessAlive() {
        synchronized (delegate) {
            try {
                return !hasExited.getBoolean(delegate);
            } catch (IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    /**
     * jdk这里应该是有个bug，可能出现process已经destroy，waitfor已经完成，但是InputStream的read操作阻塞住无法返回，最终死锁的状况。
     * InputStream的实现是BufferedInputStream，read操作会获取锁，但是会出现流已经调用close了read也无不返回-1，一直阻塞的时候；
     * 而内部有一个清理线程会在waitfor结束后开始清理，清理也需要获取stream的锁，结果就死锁了。
     * 处理方式就是每次先判断available的数量，接下来只读取available的数量，这样就不阻塞读取了。
     * jdk1.7会出现这个问题，1.8的java代码是一样的，
     * 如果InputStream read操作还是会阻塞住，那么肯定也会出现，但不确定native的变化会不会导致不再阻塞了
     */
    @Override
    public byte[] read() throws Exception {
        rateLimiter.acquire();
        InputStream inputStream = getInputStream();
        int count = readAvailableBytes(inputStream, buffer);
        if (count > 0) {
            return Arrays.copyOfRange(buffer, 0, count);
        } else {
            if (isProcessAlive()) {
                return EMPTY_BYTES;
            } else {
                return null;
            }
        }
    }

    private int readAvailableBytes(InputStream inputStream, byte[] buffer) throws IOException {
        int available = inputStream.available();
        if (available <= 0) {
            return 0;
        }
        return inputStream.read(buffer, 0, Math.min(buffer.length, available));
    }
}
