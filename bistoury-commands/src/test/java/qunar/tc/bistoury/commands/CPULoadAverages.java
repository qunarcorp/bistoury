package qunar.tc.bistoury.commands;

import java.io.InputStream;

/**
 * @author: leix.xie
 * @date: 2018/11/16 17:54
 * @describe：
 */
public class CPULoadAverages {
    /**
     * 主方法
     */
    public static void main(String[] args) throws Exception {
        Process process = Runtime.getRuntime().exec("cat /proc/loadavg");
        InputStream is = process.getInputStream();
        byte[] bytes = new byte[256];
        int read = is.read(bytes);
        byte[] result = new byte[read];
        System.arraycopy(bytes, 0, result, 0, read-1);
        System.out.println(new String(result).substring(0,read-1));
    }
}
