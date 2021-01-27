package julia.books.error;

public class NoTokenException extends RuntimeException {
    private static final long serialVersionUID = 42L;

    public NoTokenException() {
    }

    public NoTokenException(String message) {
        super(message);
    }

    public NoTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoTokenException(Throwable cause) {
        super(cause);
    }

    public NoTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
