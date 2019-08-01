package qunar.tc.bistoury.remoting.netty;


import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.common.CharsetUtils;

/**
 * @author: leix.xie
 * @date: 2019/1/9 10:56
 * @describe：
 */
public class MonitorReceiver implements ResponseHandler {

    private static final String EMPTY = "";
    private String result = EMPTY;

    public MonitorReceiver() {
    }

    public String getAndReset() {
        String current = result;
        result = EMPTY;
        return current;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void handle(String line) {
        result += line;
    }

    @Override
    public void handle(int code, String line) {
        handle(line);
    }

    @Override
    public void handle(int code, byte[] data) {
        handle(data);
    }

    @Override
    public void handle(byte[] dataBytes) {
        result += CharsetUtils.toUTF8String(dataBytes);
    }

    @Override
    public void handleError(int errorCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleError(String error) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleError(Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleEOF() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleEOF(int exitCode) {
        throw new UnsupportedOperationException();
    }

}
