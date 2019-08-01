package qunar.tc.bistoury.ui.exception;

/**
 * @author leix.xie
 * @date 2019/7/4 15:40
 * @describe
 */
public class PermissionDenyException extends RuntimeException {
    public PermissionDenyException() {
        super();
    }

    public PermissionDenyException(String message) {
        super(message);
    }

    public PermissionDenyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionDenyException(Throwable cause) {
        super(cause);
    }

    protected PermissionDenyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
