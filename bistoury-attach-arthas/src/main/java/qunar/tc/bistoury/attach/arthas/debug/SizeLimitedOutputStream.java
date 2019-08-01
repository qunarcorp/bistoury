package qunar.tc.bistoury.attach.arthas.debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 有长度限制的OutputStream
 * Created by cai.wen on 18-12-12.
 */
public class SizeLimitedOutputStream extends OutputStream {
    private final int maxSize;
    private int size;
    private final ByteArrayOutputStream byteArrayOutputStream;

    public SizeLimitedOutputStream(int maxSize) {
        this.maxSize = maxSize;
        byteArrayOutputStream = new ByteArrayOutputStream(maxSize);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        size += len;
        checkSize();
        byteArrayOutputStream.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        size += b.length;
        checkSize();
        byteArrayOutputStream.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        size++;
        checkSize();
        byteArrayOutputStream.write(b);
    }

    private void checkSize() throws SizeLimitExceededException {
        if (size > maxSize) {
            throw new SizeLimitExceededException();
        }
    }

    public synchronized byte toByteArray()[] {
        return byteArrayOutputStream.toByteArray();
    }
}
