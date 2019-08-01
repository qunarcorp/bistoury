package qunar.tc.bistoury.remoting.netty;

import qunar.tc.bistoury.agent.common.ResponseHandler;

/**
 * @author leix.xie
 * @date 2019/7/1 16:33
 * @describe
 */
public class AgentInfoPushReceiver implements ResponseHandler {
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

    }

    @Override
    public void handle(int code, String line) {

    }

    @Override
    public void handle(int code, byte[] data) {

    }

    @Override
    public void handle(byte[] dataBytes) {

    }

    @Override
    public void handleError(int errorCode) {

    }

    @Override
    public void handleError(String error) {

    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void handleEOF() {

    }

    @Override
    public void handleEOF(int exitCode) {

    }
}
