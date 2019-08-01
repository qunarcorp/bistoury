package qunar.tc.bistoury.ui.exception;

/**
 * @author leix.xie
 * @date 2019/4/26 11:47
 * @describe
 */
public class SourceFileReadException extends RuntimeException {
    public SourceFileReadException() {
    }

    public SourceFileReadException(String message) {
        super(message);
    }

    public SourceFileReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceFileReadException(Throwable cause) {
        super(cause);
    }

    public SourceFileReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
