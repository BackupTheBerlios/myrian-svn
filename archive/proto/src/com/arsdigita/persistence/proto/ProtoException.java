package com.arsdigita.persistence.proto;

/**
 * This is the base class for exceptions in persistence. By default these
 * exceptions are assumed to be internal exceptions - bugs in the
 * implementation. If code in the implementation determines the exception is
 * fault of the client, the exception is flagged so that it is not internal.
 * At the top level internal exceptions are converted to
 * UncheckedWrapperExceptions so that they will not be caught as persistence
 * exceptions.
 **/
public class ProtoException extends RuntimeException {

    // internal or external exception
    private boolean m_internal = true;

    ProtoException() { super(); }

    ProtoException(boolean internal) {
        super();
        setInternal(internal);
    }

    ProtoException(String msg) { super(msg); }

    ProtoException(String msg, boolean internal) {
        super(msg);
        setInternal(internal);
    }

    void setInternal(boolean internal) { m_internal = internal; }

    boolean isInternal() { return m_internal; }
}
