package qunar.tc.bistoury.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * @author: leix.xie
 * @date: 2018/11/8 17:27
 * @describe：
 */
public class GZFile {
    private static final Logger LOG = LoggerFactory.getLogger(GZFile.class);

    /**
     * 主方法
     */
    public static void main(String[] args) throws Exception {
        File file = new File("D:\\localhost.2018-10-31.log.gz");
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        if (isGZipFile(stream)) {
            GZIPInputStream ungzip = new GZIPInputStream(stream);
            InputStream inputStream = ungzip;
            OutputStream fos = new FileOutputStream("D:\\1.txt");
            byte[] b = new byte[1024];
            while (inputStream.read(b, 0, 1024) != -1) {
                fos.write(b, 0, 1024);
            }
            fos.flush();
        } else if (isGZipFile(stream)) {

        }
    }

    private static int readUShort(InputStream in) throws IOException {
        int b = readUByte(in);
        return (readUByte(in) << 8) | b;
    }

    private static int readUByte(InputStream in) throws IOException {
        int b = in.read();
        if (b == -1) {
            throw new EOFException();
        }
        if (b < -1 || b > 255) {
            // Report on this.in, not argument in; see read{Header, Trailer}.
            throw new IOException(in.getClass().getName()
                    + ".read() returned value out of range -1..255: " + b);
        }
        return b;
    }

    public static int getMagic(InputStream in) throws Exception {
        if (!in.markSupported()) {
            return -1;
        }
        in.mark(0);
        int b = readUByte(in);
        int result = readUByte(in);
        if (b == -1 || result == -1) {
            return -1;
        }
        in.reset();
        return (result << 8) | b;
    }

    private static boolean isGZipFile(InputStream in) {
        if (!in.markSupported()) {
            return false;
        }
        in.mark(0);
        try {
            int b = readUByte(in);
            int b2 = readUByte(in);
            return ((b2 << 8) | b) == GZIPInputStream.GZIP_MAGIC;
        } catch (IOException e) {
            LOG.error("get gzip magic error", e);
        } finally {
            try {
                in.reset();
            } catch (Exception e) {
                LOG.error("reset inputStream error", e);
                return false;
            }
        }
        return false;
    }
}
