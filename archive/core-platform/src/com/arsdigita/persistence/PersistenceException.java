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

package com.arsdigita.persistence;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.db.DbException;
import com.arsdigita.persistence.proto.ProtoException;

/**
 * PersistenceException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/07/02 $
 */

public class PersistenceException extends UncheckedWrapperException {

    private String m_messageStack = null;

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/PersistenceException.java#5 $ by $Author: ashah $, $DateTime: 2003/07/02 17:18:32 $";

    /**
     * Constructor for a PersistenceException which does not wrap
     * another exception.  If wrapping another exception,
     * PersistenceException.newInstance(...) should be used.
     *
     * @see #newInstance(Throwable)
     */
    public PersistenceException(String message) {
        super(message, null);
    }

    /**
     * Constructor which takes a root cause
     * that this exception will be wrapping.
     */
    protected PersistenceException(Throwable rootCause) {
        this(SessionManager.getSession().getStackTrace(), rootCause);
    }

    /**
     * Constructor which takes a message string and a root cause
     * that this exception will be wrapping.  The message string
     * should be something different than rootCause.getMessage()
     * would normally provide.
     */
    protected PersistenceException(String s, Throwable rootCause) {
        super((s==null)?(SessionManager.getSession().getStackTrace()):
              (s + SessionManager.getSession().getStackTrace()),
              rootCause);
        // TODO: Consider adding verification that if rootCause is
        // a uniqueconstraintexception or dbnotavailableexception,
        // then so is this persistenceexception.  To guard against
        // people calling this constructor inappropriately.
        // Not doing right now because it a) seems like overkill,
        // b) could result in a legit error message being eaten and
        // replaced with something else entirely at runtime when
        // weird exceptions happened; i.e. it would be hard to
        // fully test.
    }

    /**
     *  This overrides the super getMessage so that it will print out
     *  the debugging messages for the stack trace
     */
    public String getMessage() {
        // by holding it in a local variable, you can all getMessage
        // a bunch of times and always get the same message for the given
        // Exception
        if (m_messageStack == null) {
            m_messageStack = SessionManager.getSession().getStackTrace();
        }
        String msg = super.getMessage();
        if (msg != null) {
            return msg + m_messageStack;
        } else {
            return m_messageStack;
        }
    }

    /**
     * It's not necessary to use newInstance for just a string argument, but
     * you can if you'd prefer to remain consistent in PersistenceException
     * creation.
     */
    public static final PersistenceException newInstance(String s) {
        return new PersistenceException(s);
    }

    /**
     * This method should be used to create a persistence exception wrapping
     * another exception, to allow for creation of a particular subtype of
     * persistence exception based on the type of the rootCause passed in.
     */
    public static final PersistenceException newInstance(Throwable rootCause) {
        return newInstance(null, rootCause);
    }

    /**
     * This method should be used to create a persistence exception wrapping
     * another exception, to allow for creation of a particular subtype of
     * persistence exception based on the type of the rootCause passed in.
     */
    public static final PersistenceException newInstance(String s,
                                                         Throwable rootCause) {
        if (rootCause instanceof DbException) {
            if (rootCause instanceof
                com.arsdigita.db.UniqueConstraintException) {
                return new UniqueConstraintException(s, (DbException)rootCause);
            } else if (rootCause instanceof
                       com.arsdigita.db.DbNotAvailableException) {
                return new DbNotAvailableException(s, (DbException)rootCause);
            }
        } else if (rootCause instanceof
                   com.arsdigita.persistence.proto.DuplicateObjectException) {
            return new UniqueConstraintException
                (s, (com.arsdigita.persistence.proto.DuplicateObjectException)
                 rootCause);
        }
        return new PersistenceException(s, rootCause);
    }
}
