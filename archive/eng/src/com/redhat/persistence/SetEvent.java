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
package com.redhat.persistence;

import com.redhat.persistence.metadata.Property;

/**
 * SetEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class SetEvent extends PropertyEvent {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/SetEvent.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Object m_oldValue;

    SetEvent(Session ssn, Object obj, Property prop, Object arg) {
        super(ssn, obj, prop, arg);
    }

    SetEvent(Session ssn, Object obj, Property prop, Object arg,
             PropertyEvent origin) {
        super(ssn, obj, prop, arg, origin);
    }

    public void dispatch(Switch sw) {
        sw.onSet(this);
    }

    void activate() {
        PropertyEvent pe = getSession().getEventStream().getLastEvent(this);
        if (pe != null) {
            m_oldValue = pe.getArgument();
        } else {
            m_oldValue = getSession().get(getObject(), getProperty());
        }

        super.activate();

        // PD dependencies
        if (getArgument() != null) {
            getPropertyData().transferNotNullDependentEvents(this);
        } else if (!getProperty().isNullable()) {
            getPropertyData().addNotNullDependent(this);
        }
    }

    void sync() {
        super.sync();
        PropertyData pd = getPropertyData();
        pd.setValue(getArgument());
    }

    public Object getPreviousValue() { return m_oldValue; }

    public String getName() { return "set"; }

}
