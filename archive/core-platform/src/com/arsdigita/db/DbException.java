/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.db;

import com.arsdigita.util.UncheckedWrapperException;

import java.sql.SQLException;

/**
 * An exception class that can be used to indicate a generic DB Exception, 
 * or subclassed and used to wrap a SQLException with a more-specific
 * error type.
 */
public class DbException extends SQLException {

    public static final String versionId
        = "$Id: //core-platform/dev/src/com/arsdigita/db/DbException.java#2 $";

    private UncheckedWrapperException m_delegate = null;

    public DbException () {
        this(null, null);
    }

    public DbException (String msg) {
        this(msg, null);
    }

    public DbException (Throwable rootCause) {
        this (null, rootCause);
    }

    public DbException (String msg, Throwable rootCause) {
        setRootCause(msg, rootCause);
    }

    public void setRootCause(String msg) {
        setRootCause(msg, null);
    }

    public void setRootCause(Throwable rootCause) {
        setRootCause(null, rootCause);
    }

    public void setRootCause(String msg, Throwable rootCause) {
        m_delegate = new UncheckedWrapperException(msg, rootCause);        
    }

    /**
     * Indicates if this exception has a root cause.
     */
    public boolean hasRootCause() {
        return (m_delegate != null);
    }

    /**
     * Gets the root cause of this exception.
     */
    public Throwable getRootCause() {
        return m_delegate.getRootCause();
    }

    // All further methods override normal throwable behavior to 
    // combine information w/ the root cause.

    /**
     * String representing this exception and the root cause.
     */
    public String toString() {
        return m_delegate.toString(this.getClass());
    }

    /**
     * This exception's message and the root cause's.
     */
    public String getMessage() {
        return m_delegate.getMessage();
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace() {
        m_delegate.printStackTrace();
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace(java.io.PrintStream s) {
        m_delegate.printStackTrace(s);
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace(java.io.PrintWriter s) {
        m_delegate.printStackTrace(s);
    }
}
