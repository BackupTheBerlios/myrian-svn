/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence;

import org.myrian.persistence.metadata.ObjectType;
import org.myrian.persistence.metadata.Role;
import java.util.Iterator;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //eng/persistence/dev/src/org/myrian/persistence/CreateEvent.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

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

        // unkeyed object violations
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
