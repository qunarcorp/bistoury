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

import java.io.InputStream;
import java.util.Arrays;

/**
 * @author zhenyu.nie created on 2019 2019/7/16 18:53
 */
public class NormalProcess extends ClosableProcess {

    private static final int BUF_SIZE = 4 * 1024;

    private final RateLimiter rateLimiter = RateLimiter.create(16); //限制每秒read的次数

    NormalProcess(Process delegate) {
        super(delegate);
    }

    @Override
    public int readAndWaitFor(ResponseHandler handler) throws Exception {
        try (InputStream inputStream = getInputStream()) {
            byte[] buffer = new byte[BUF_SIZE];
            while (true) {
                rateLimiter.acquire();
                int count = inputStream.read(buffer);
                if (count > 0) {
                    handler.handle(Arrays.copyOfRange(buffer, 0, count));
                } else if (count < 0) {
                    break;
                }
            }
        }
        return waitFor();
    }
}