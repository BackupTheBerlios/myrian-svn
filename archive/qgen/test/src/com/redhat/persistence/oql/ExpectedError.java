package com.redhat.persistence.oql;

final class ExpectedError {
    private final String m_error;
    private final String m_msg;

    ExpectedError(String error, String msg) {
	m_error = error;
	if(msg != null) {
	    msg = msg.trim();
	}
	m_msg = msg;
    }

    boolean isExpected(final Throwable error) {
	boolean isExpected = false;
	if(m_error.equals(error.getClass().getName())) {
	    isExpected = true;
	    if(m_msg != null) {
		isExpected = m_msg.equals(error.getMessage().trim());
		
	    }
	}
	return isExpected;
    }
}
