package com.redhat.test;

/**
 * Note that this is an unchecked exception.
 *
 * @since 2004-05-21
 * @author Vadim Nasardinov (vadimn@redhat.com)
 **/
public class YAdapterException extends RuntimeException {
    public YAdapterException(Throwable cause) {
        super(cause);
    }

    public YAdapterException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public YAdapterException(String msg) {
        super(msg);
    }
}
