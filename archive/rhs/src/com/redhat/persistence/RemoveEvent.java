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

import com.redhat.persistence.metadata.Property;

/**
 * RemoveEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/04/05 $
 **/

public class RemoveEvent extends PropertyEvent {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/RemoveEvent.java#2 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

    RemoveEvent(Session ssn, Object obj, Property prop, Object arg) {
        this(ssn, obj, prop, arg, null);
    }

    RemoveEvent(Session ssn, Object obj, Property prop, Object arg,
                PropertyEvent origin) {
        super(ssn, obj, prop, arg, origin);
        if (arg == null) { throw new IllegalArgumentException(toString()); }
    }

    public void dispatch(Switch sw) {
        sw.onRemove(this);
    }

    public String getName() { return "remove"; }

}
