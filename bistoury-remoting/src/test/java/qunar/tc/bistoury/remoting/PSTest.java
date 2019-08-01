package qunar.tc.bistoury.remoting;

import com.google.common.base.Strings;

import java.io.InputStream;
import java.util.Arrays;

/**
 * @author: leix.xie
 * @date: 2019/3/7 10:54
 * @describe：
 */
public class PSTest {
    private static final int BUF_SIZE = 1024;
    private static final int USER_INDEX = 0;
    private static final int PID_INDEX = 1;
    private static final int COMMAND_INDEX = 10;
    private static byte[] buffer = new byte[BUF_SIZE];
    private static int writerIndex = 0;

    /**
     * 主方法
     */
    public static void main(String[] args) {
        final String ps = ps();
        parse(ps);
    }

    public static void parse(final String ps) {
        String all = ps.replaceAll("[( )\t]+", " ");
        String[] lines = all.split("[\n\r(\r\n)]");
        for (String line : lines) {
            if (Strings.isNullOrEmpty(line)) {
                continue;
            }
            String[] pieces = line.split(" ");
            final String user = pieces[USER_INDEX];
            final int pid = Integer.parseInt(pieces[PID_INDEX]);
            final String command = pieces[COMMAND_INDEX];
            final String[] params = Arrays.copyOfRange(pieces, COMMAND_INDEX + 1, pieces.length);
            Process process = new Process(user, pid, command, params);
            // multimap.put(command, process);
            System.out.println(process);
        }
        System.out.println();
    }

    public static String ps() {
        try {
            java.lang.Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "ps -aux | grep java"});
            InputStream inputStream = process.getInputStream();
            byte[] bytes = new byte[BUF_SIZE];
            writerIndex = 0;
            int readBytesCount = inputStream.read(bytes);
            while (readBytesCount > -1) {
                if (writerIndex + 1 + readBytesCount > buffer.length) {
                    byte[] oldBuff = buffer;
                    buffer = new byte[buffer.length * 2];
                    System.arraycopy(oldBuff, 0, buffer, 0, writerIndex);
                }
                System.arraycopy(bytes, 0, buffer, writerIndex, readBytesCount);
                writerIndex += readBytesCount;
                readBytesCount = inputStream.read(bytes);
            }
            process.waitFor();
            String res = new String(Arrays.copyOf(buffer, writerIndex));
            System.out.println(res);
            System.out.println("==============================================");
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

class Process {
    private String user;
    private int pid;
    private String command;
    private String[] params;

    public Process(String user, int pid, String command, String[] params) {
        this.user = user;
        this.pid = pid;
        this.command = command;
        this.params = params;
    }

    @Override
    public String toString() {
        return "Process{" +
                "user='" + user + '\'' +
                ", pid=" + pid +
                ", command='" + command + '\'' +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}