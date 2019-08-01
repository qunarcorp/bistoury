package qunar.tc.bistoury.common;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import org.jboss.netty.channel.socket.nio.NioClientBossPool;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.ThreadNameDeterminer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhenyu.nie created on 2017 2017/8/25 17:26
 */
public class AsyncHttpClientHolder {

    private static final AsyncHttpClient INSTANCE = initClient();

    private static final int CONN_TIMEOUT = 1000;
    private static final int REQUEST_TIMEOUT = 2000;
    private static final int READ_TIMEOUT = Integer.MAX_VALUE;

    private static final int BOSS_COUNT = 1;
    private static final int WORKER_COUNT = 2;

    public static AsyncHttpClient getInstance() {
        return INSTANCE;
    }

    public static synchronized void close() {
        INSTANCE.close();
    }

    private static AsyncHttpClient initClient() {
        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setConnectTimeout(CONN_TIMEOUT);
        builder.setRequestTimeout(REQUEST_TIMEOUT);
        builder.setReadTimeout(READ_TIMEOUT);
        builder.setAllowPoolingConnections(true);
        builder.setCompressionEnforced(true);
        builder.setPooledConnectionIdleTimeout(3 * 60 * 1000);

        ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "async-http-callback");
                t.setDaemon(true);
                return t;
            }
        });
        builder.setExecutorService(threadPool);

        NettyAsyncHttpProviderConfig providerConfig = new NettyAsyncHttpProviderConfig();
        NioClientBossPool bossPool = new NioClientBossPool(threadPool, BOSS_COUNT, new HashedWheelTimer(), new ThreadNameDeterminer() {
            @Override
            public String determineThreadName(String currentThreadName, String proposedThreadName) throws Exception {
                return "async-http boss #1";
            }
        });
        NioWorkerPool workerPool = new NioWorkerPool(threadPool, WORKER_COUNT, new ThreadNameDeterminer() {
            private final AtomicInteger i = new AtomicInteger(0);

            @Override
            public String determineThreadName(String currentThreadName, String proposedThreadName) throws Exception {
                return "async-http worker #" + i.incrementAndGet();
            }
        });
        providerConfig.setSocketChannelFactory(new NioClientSocketChannelFactory(bossPool, workerPool));
        builder.setAsyncHttpClientProviderConfig(providerConfig);
        return new AsyncHttpClient(builder.build());
    }
}
