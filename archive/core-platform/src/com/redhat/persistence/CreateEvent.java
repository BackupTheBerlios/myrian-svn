/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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

package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Role;
import java.io.*;
import java.util.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/10/21 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/CreateEvent.java#3 $ by $Author: ashah $, $DateTime: 2003/10/21 11:38:25 $";

    CreateEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onCreate(this);
    }

    void activate() {
        super.activate();

        getObjectData().setState(ObjectData.INFANTILE);

        // set up new dependencies
        ObjectType type = getSession().getObjectType(getObject());

        for (Iterator it = type.getRoles().iterator(); it.hasNext(); ) {
            Role role = (Role) it.next();
            if (!role.isNullable()) {
                PropertyData pd =
                    getSession().fetchPropertyData(getObject(), role);
                pd.addNotNullDependent(this);
            }
        }
    }

    void sync() {
        super.sync();
        getSession().addObjectData(getObjectData());
        getObjectData().setState(ObjectData.AGILE);
    }

    String getName() { return "create"; }
}
