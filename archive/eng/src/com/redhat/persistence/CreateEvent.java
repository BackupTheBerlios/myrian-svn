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

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Role;
import java.util.Iterator;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2004/08/30 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/CreateEvent.java#5 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    CreateEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onCreate(this);
    }

    void activate() {
        super.activate();

        ObjectData odata = getObjectData();
        odata.setObject(getObject());
        odata.setState(ObjectData.INFANTILE);

        Session ssn = getSession();

        // set up new dependencies
        ObjectType type = ssn.getObjectType(getObject());

        for (Iterator it = type.getRoles().iterator(); it.hasNext(); ) {
            Role role = (Role) it.next();
            if (!role.isNullable()) {
                PropertyData pd = ssn.fetchPropertyData(getObject(), role);
                pd.addNotNullDependent(this);
            }
        }

        // nested object violations
        if (!ssn.hasSessionKey(getObject())) {
            ssn.addViolation(odata);
        }
    }

    void sync() {
        super.sync();
        getObjectData().setState(ObjectData.AGILE);
    }

    String getName() { return "create"; }
}
