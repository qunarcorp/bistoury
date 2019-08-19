package qunar.tc.bistoury.agent.common.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

/**
 * @author xkrivzooh
 * @since 2019/8/19
 */
public class NetWorkUtils {

    private static final int MAX_PORT = 65535;

    private static final int RND_PORT_START = 30000;

    private static final int RND_PORT_RANGE = 10000;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static int getAvailablePort() {
        return getAvailablePort(0);
    }

    public static int getAvailablePort(int port) {
        if (port <= 0) {
            port = getRandomPort();
        }
        for (int i = port; i < MAX_PORT; i++) {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(i);
                return i;
            }
            catch (IOException e) {
                // continue
            }
            finally {
                if (ss != null) {
                    try {
                        ss.close();
                    }
                    catch (IOException ignored) {
                    }
                }
            }
        }
        return port;
    }

    private static int getRandomPort() {
        return RND_PORT_START + RANDOM.nextInt(RND_PORT_RANGE);
    }

}

