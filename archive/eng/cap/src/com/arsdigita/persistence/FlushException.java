/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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

/**
 * An exception representing the inability to write all changes to the
 * database. It is caused by some required properties being set to null.
 */
public class FlushException extends PersistenceException {

    public static final String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/FlushException.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    /**
     * No public constructor.
     * Should only be constructed by PersistenceException.newInstance.
     */
    protected FlushException (com.redhat.persistence.FlushException e) {
        super(e);
    }

    /**
     * No public constructor.
     * Should only be constructed by PersistenceException.newInstance.
     */
    protected FlushException(String msg,
                             com.redhat.persistence.FlushException e) {
        super(msg, e);
    }

}
