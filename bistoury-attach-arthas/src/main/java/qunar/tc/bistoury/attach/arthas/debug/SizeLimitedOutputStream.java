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

import java.io.IOException;
import java.io.OutputStream;

/**
 * 有长度限制的OutputStream
 * Created by cai.wen on 18-12-12.
 */
class SizeLimitedOutputStream extends OutputStream {

    private final OutputStream outputStream;

    private final int maxSize;

    private int size;

    SizeLimitedOutputStream(OutputStream outputStream, int maxSize) {
        this.outputStream = outputStream;
        this.maxSize = maxSize;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int newSize = size + len;
        checkSize(newSize);
        outputStream.write(b, off, len);
        size = newSize;
    }

    @Override
    public void write(byte[] b) throws IOException {
        int newSize = size + b.length;
        checkSize(newSize);
        outputStream.write(b);
        size = newSize;
    }

    @Override
    public void write(int b) throws IOException {
        int newSize = size + 1;
        checkSize(newSize);
        outputStream.write(b);
        size = newSize;
    }

    private void checkSize(int newSize) throws SizeLimitExceededException {
        if (newSize > maxSize) {
            throw new SizeLimitExceededException();
        }
    }
}
