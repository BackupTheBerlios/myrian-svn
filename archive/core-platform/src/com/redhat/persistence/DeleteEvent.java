/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.util.Iterator;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2004/03/30 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/DeleteEvent.java#4 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    DeleteEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onDelete(this);
    }

    void activate() {
        super.activate();
        ObjectType type = getSession().getObjectType(getObject());

        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            PropertyData pd = getObjectData().getPropertyData(prop);
            if  (pd == null) { continue; }

            if (!prop.isNullable()) {
                pd.transferNotNullDependentEvents(this);
            }

            if (prop.isCollection()) {
                for (Iterator evs = getSession().getEventStream().
                         getCurrentEvents(getObject(), prop).iterator();
                     evs.hasNext(); ) {
                    ((PropertyEvent) evs.next()).addDependent(this);
                }
            } else {
                Event ev = getSession().getEventStream().getLastEvent
                    (getObject(), prop);
                if (ev != null) { ev.addDependent(this); }
            }
        }

        getObjectData().setState(ObjectData.SENILE);
    }

    void sync() {
        super.sync();
        getObjectData().setState(ObjectData.DEAD);
    }

    String getName() { return "delete"; }

}
