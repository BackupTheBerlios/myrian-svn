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

import com.redhat.persistence.metadata.Property;

/**
 * AddEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class AddEvent extends PropertyEvent {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/AddEvent.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    AddEvent(Session ssn, Object obj, Property prop, Object arg) {
        this(ssn, obj, prop, arg, null);
    }

    AddEvent(Session ssn, Object obj, Property prop, Object arg,
             PropertyEvent origin) {
        super(ssn, obj, prop, arg, origin);
        if (arg == null) { throw new IllegalArgumentException(toString()); }
    }

    public void dispatch(Switch sw) {
        sw.onAdd(this);
    }

    public String getName() { return "add"; }

}
