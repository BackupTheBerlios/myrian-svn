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
package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import java.util.*;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.*;

import org.apache.log4j.Logger;

public class JDOAdapter extends Adapter {
    private static final Logger s_log = Logger.getLogger(JDOAdapter.class);

    public Object getObject(ObjectType base, PropertyMap props, Session ssn) {
        if (ssn == null) { throw new NullPointerException("ssn"); }
        PersistenceManagerImpl pmi = getPMI(ssn);
        return pmi.newPC(props);
    }

    public PropertyMap getProperties(Object obj) {
        if (!(obj instanceof PersistenceCapable)) {
            throw new ClassCastException
                ("expected PersistenceCapable: " + obj);
        }

        PersistenceManager pm = ((PersistenceCapable) obj).jdoGetPersistenceManager();

        if (!(pm instanceof PersistenceManagerImpl)) {
            throw new ClassCastException
                ("expected PersistenceManagerImpl: " + pm);
        }

        return ((PersistenceManagerImpl) pm).
            getStateManager((PersistenceCapable) obj).getPropertyMap();
    }

    public ObjectType getObjectType(Object obj) {
        if (obj == null) { throw new NullPointerException("obj"); }
        return getProperties(obj).getObjectType();
    }

    private static PersistenceManagerImpl getPMI(Session ssn) {
        PersistenceManagerImpl pmi = null;
        synchronized (ssn) {
            return (PersistenceManagerImpl) ssn.getAttribute(pmi.ATTR_NAME);
        }
    }
}
