/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence;

import com.arsdigita.util.UncheckedWrapperException;

/**
 * PersistenceException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */

public class PersistenceException extends UncheckedWrapperException {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/PersistenceException.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
        this(null, rootCause);
    }

    /**
     * Constructor which takes a message string and a root cause
     * that this exception will be wrapping.  The message string
     * should be something different than rootCause.getMessage()
     * would normally provide.
     */
    protected PersistenceException(String s, Throwable rootCause) {
        super(s, rootCause);
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
        if (rootCause instanceof com.redhat.persistence.FlushException) {
            return new FlushException
                (s, (com.redhat.persistence.FlushException) rootCause);
        }
        return new PersistenceException(s, rootCause);
    }
}
