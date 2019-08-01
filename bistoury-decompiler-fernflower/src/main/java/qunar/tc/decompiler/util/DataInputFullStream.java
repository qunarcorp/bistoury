// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package qunar.tc.decompiler.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataInputFullStream extends DataInputStream {
    public DataInputFullStream(byte[] bytes) {
        super(new ByteArrayInputStream(bytes));
    }

    public DataInputFullStream(InputStream in) {
        super(in);
    }

    public byte[] read(int n) throws IOException {
        return InterpreterUtil.readBytes(this, n);
    }

    public void discard(int n) throws IOException {
        InterpreterUtil.discardBytes(this, n);
    }
}