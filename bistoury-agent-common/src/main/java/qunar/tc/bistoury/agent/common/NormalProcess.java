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