package qunar.tc.bistoury.ui.exception;

/**
 * @author leix.xie
 * @date 2019/4/26 11:47
 * @describe
 */
public class SourceFileNotFoundException extends RuntimeException {

    public SourceFileNotFoundException() {
    }

    public SourceFileNotFoundException(String message) {
        super(message);
    }

    public SourceFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public SourceFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
