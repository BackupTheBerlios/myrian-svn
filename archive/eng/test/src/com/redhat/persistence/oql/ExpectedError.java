/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
	if (m_error.equals(error.getClass().getName())) {
	    isExpected = true;
	    if (m_msg != null) {
		isExpected = m_msg.equals(error.getMessage().trim());
	    }
	}
        if (isExpected) {
            return true;
        } else {
            Throwable cause = error.getCause();
            if (cause != null) {
                return isExpected(cause);
            } else {
                return false;
            }
        }
    }
}
