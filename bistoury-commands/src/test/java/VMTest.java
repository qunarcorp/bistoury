import java.io.File;
import java.io.FileInputStream;

/**
 * @author: leix.xie
 * @date: 2018/12/3 20:18
 * @describe：
 */
public class VMTest {
    /**
     * 主方法
     */
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        File file = new File("C:\\Users\\leixie\\.IntelliJIdea2018.2\\system\\tomcat\\Unnamed_realtimelogviewer\\logs\\gc.log");
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[10240];
        int readBytesCount = inputStream.read(buffer);
        while (readBytesCount > -1) {
            readBytesCount = inputStream.read(buffer);
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
