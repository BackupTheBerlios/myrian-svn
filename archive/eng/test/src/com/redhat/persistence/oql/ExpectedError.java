/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
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
