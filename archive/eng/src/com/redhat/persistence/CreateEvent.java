/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Role;
import java.util.Iterator;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2004/08/06 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/CreateEvent.java#3 $ by $Author: rhs $, $DateTime: 2004/08/06 08:43:09 $";

    CreateEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onCreate(this);
    }

    void activate() {
        super.activate();

        ObjectData odata = getObjectData();
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
