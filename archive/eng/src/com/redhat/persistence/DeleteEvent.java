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
import com.redhat.persistence.metadata.Property;
import java.util.Iterator;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/DeleteEvent.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    DeleteEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onDelete(this);
    }

    void activate() {
        super.activate();
        Session ssn = getSession();
        ObjectType type = ssn.getObjectType(getObject());
        ObjectData odata = getObjectData();

        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            PropertyData pd = odata.getPropertyData(prop);
            if  (pd == null) { continue; }

            if (!prop.isNullable()) {
                pd.transferNotNullDependentEvents(this);
            }

            if (prop.isCollection()) {
                for (Iterator evs = ssn.getEventStream().
                         getCurrentEvents(getObject(), prop).iterator();
                     evs.hasNext(); ) {
                    ((PropertyEvent) evs.next()).addDependent(this);
                }
            } else {
                Event ev = ssn.getEventStream().getLastEvent
                    (getObject(), prop);
                if (ev != null) { ev.addDependent(this); }
            }
        }

        odata.setState(ObjectData.SENILE);

        // clean up nested object violations
        ssn.removeViolation(odata);
    }

    void sync() {
        super.sync();
        getObjectData().setState(ObjectData.DEAD);
    }

    String getName() { return "delete"; }

}
