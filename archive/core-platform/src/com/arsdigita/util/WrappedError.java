/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.util;

import org.apache.log4j.Logger;

/**
 * A wrapper error that can be used to rethrow another throwable.
 *
 * TODO: This should become a skeleton when/if we switch to Java 1.4.
 * http://java.sun.com/j2se/1.4/docs/guide/lang/chained-exceptions.html
 *
 * The basic exception methods are overridden with methods that
 * combine this wrapper and its root cause, so it can be
 * treated just like any normal exception in actual use.
 *
 * @since   2004-01-19
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/WrappedError.java#4 $
 */
public class WrappedError extends Error {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/WrappedError.java#4 $";

    static {
        Exceptions.registerUnwrapper(
            WrappedError.class,
            new ExceptionUnwrapper() {
                public Throwable unwrap(Throwable t) {
                    WrappedError ex = (WrappedError)t;
                    return ex.getRootCause();
                }
            });
    }

    Throwable m_rootCause;

    /**
     * Constructor which only takes a msg, which will cause this
     * WrappedError to behave like a normal RuntimeException.
     * While it doesn't seem to make a lot of sense to have a wrapper
     * exception that doesn't wrap anything, this is needed so that it
     * can be used as a direct replacement for RuntimeException.
     */
    public WrappedError (String msg) {
        this(msg, null);
    }

    /**
     * Constructor which takes a root cause
     * that this exception will be wrapping.
     */
    public WrappedError (Throwable rootCause) {
        this(null, rootCause);
    }

    /**
     * Constructor which takes a message string and a root cause
     * that this exception will be wrapping.  The message string
     * should be something different than rootCause.getMessage()
     * would normally provide.
     */
    public WrappedError (String s, Throwable rootCause) {
        super(s);
        this.m_rootCause = rootCause;
    }

    /**
     * Throws an WrappedError, and ensurs that it is logged at ERROR priority.
     *
     * @param source Class having the error. For Log4J reporting
     * @param msg Error message
     * @param rootCause The root cause exception
     *
     * @throws WrappedError
     */
    public static void throwLoggedException(Class source, String msg, Throwable rootCause) throws WrappedError {
        Logger log = Logger.getLogger(source);
        log.error(msg, rootCause);

        throw new WrappedError(msg, rootCause);
    }

    /**
     * Indicates if this exception has a root cause.
     */
    public boolean hasRootCause() {
        return m_rootCause != null;
    }

    /**
     * Gets the root cause of this exception.
     */
    public Throwable getRootCause() {
        return m_rootCause;
    }

    // All further methods override normal throwable behavior to
    // combine information w/ the root cause.

    /**
     * Get a string representing this exception and the root cause.
     */
    public String toString() {
        return toString(this.getClass());
    }

    /**
     * Get a string representing this exception and the root cause.
     *
     * Functions like normal toString, except that the name of the
     * provided class will be used instead of the name of the
     * unchecked wrapper exception.  Useful when another exception
     * class is using  an unchecked wrapper exception to delegate
     * to.
     */
    public String toString(Class delegatingClass) {
        // default toString calls getMessage, so we don't want to rely on it
        // here.
        StringBuffer b = new StringBuffer(delegatingClass.getName());
        String superMsg = super.getMessage();
        if (superMsg != null) {
            b.append(": ").append(superMsg);
        }
        if (m_rootCause != null) {
            b.append(" (root cause: ").append(m_rootCause.toString());
            b.append(")");
        }
        return b.toString();
    }

    /**
     * This exception's message and the root cause's.
     */
    public String getMessage() {
        if (m_rootCause != null) {
            return super.getMessage() + " (root cause: " + m_rootCause.getMessage() + ")";
        } else {
            return super.getMessage();
        }
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace() {
        super.printStackTrace();
        if (m_rootCause != null) {
            System.err.print("Root cause: ");
            m_rootCause.printStackTrace();
        }
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace(java.io.PrintStream s) {
        super.printStackTrace(s);
        if (m_rootCause != null) {
            s.println("Root cause: ");
            m_rootCause.printStackTrace(s);
        }
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace(java.io.PrintWriter s) {
        super.printStackTrace(s);
        if (m_rootCause != null) {
            s.println("Root cause: ");
            m_rootCause.printStackTrace(s);
        }
    }
}
