/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util;

import org.apache.log4j.Logger;

/**
 * A wrapper exception that can be used to rethrow another
 * exception.
 *
 * TODO: This should become a skeleton when/if we switch to Java 1.4.
 * http://java.sun.com/j2se/1.4/docs/guide/lang/chained-exceptions.html
 *
 * The basic exception methods are overridden with methods that
 * combine this wrapper and its root cause, so it can be
 * treated just like any normal exception in actual use.
 *
 * Note that it is not necessary to provide a string along
 * with a root cause; in particular, the following usage:
 * <tt>new UncheckedWrapperException(e);</tt> is more correct than
 * <tt>new UncheckedWrapperException(e.getMessage(), e);</tt>
 *
 * @author David Eison
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/UncheckedWrapperException.java#5 $
 */
public class UncheckedWrapperException extends RuntimeException {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/UncheckedWrapperException.java#5 $";

    Throwable m_rootCause;

    /**
     * Constructor which only takes a msg, which will cause this
     * UncheckedWrapperException to behave like a normal RuntimeException.
     * While it doesn't seem to make a lot of sense to have a wrapper
     * exception that doesn't wrap anything, this is needed so that it
     * can be used as a direct replacement for RuntimeException.
     */
    public UncheckedWrapperException (String msg) {
        this(msg, null);
    }

    /**
     * Constructor which takes a root cause
     * that this exception will be wrapping.
     */
    public UncheckedWrapperException (Throwable rootCause) {
        this(null, rootCause);
    }

    /**
     * Constructor which takes a message string and a root cause
     * that this exception will be wrapping.  The message string
     * should be something different than rootCause.getMessage()
     * would normally provide.
     */
    public UncheckedWrapperException (String s, Throwable rootCause) {
        super(s);
        this.m_rootCause = rootCause;
    }

    /**
     * Throws an UncheckedWrapperException, and ensurs that it is logged at ERROR priority.
     *
     * @param source Class having the error. For Log4J reporting
     * @param msg Error message
     * @param rootCause The root cause exception
     *
     * @throws UncheckedWrapperException
     */
    public static void throwLoggedException(Class source, String msg, Throwable rootCause) throws UncheckedWrapperException {
        Logger log = Logger.getLogger(source);
        log.error(msg, rootCause);

        throw new UncheckedWrapperException(msg, rootCause);
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
