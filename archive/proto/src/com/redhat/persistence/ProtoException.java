package com.redhat.persistence;

/**
 * This is the base class for exceptions in persistence. By default these
 * exceptions are assumed to be internal exceptions - bugs in the
 * implementation. If code in the implementation determines the exception is
 * fault of the client, the exception is flagged so that it is not internal.
 * At the top level internal exceptions are converted to
 * UncheckedWrapperExceptions so that they will not be caught as persistence
 * exceptions.
 **/
public abstract class ProtoException extends RuntimeException {

    public static class Role {

	private String m_name;

	Role(String name) {
	    m_name = name;
	}

	public String toString() {
	    return m_name;
	}

    }

    public static final Role OBJECT = new Role("object");
    public static final Role PROPERTY = new Role("property");
    public static final Role VALUE = new Role("value");

    // internal or external exception
    private boolean m_internal = true;

    protected ProtoException() { super(); }

    protected ProtoException(boolean internal) {
        super();
        setInternal(internal);
    }

    protected ProtoException(String msg) { super(msg); }

    protected ProtoException(String msg, boolean internal) {
        super(msg);
        setInternal(internal);
    }

    void setInternal(boolean internal) { m_internal = internal; }

    boolean isInternal() { return m_internal; }
}
