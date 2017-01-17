package io.github.biezhi.session.exception;

/**
 * Created by biezhi on 2016/12/17.
 */
public class SessionException extends RuntimeException {

    public SessionException() {
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionException(Throwable cause) {
        super(cause);
    }

}
