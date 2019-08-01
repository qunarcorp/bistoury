package qunar.tc.bistoury.agent.common;

/**
 * @author sen.chai
 * @date 15-6-15
 */
public interface ResponseHandler {

    boolean isWritable();

    boolean isActive();

    void handle(String line);

    void handle(int code, String line);

    void handle(int code, byte[] data);

    void handle(byte[] dataBytes);

    void handleError(int errorCode);

    void handleError(String error);

    void handleError(Throwable throwable);

    void handleEOF();

    void handleEOF(int exitCode);

}

    