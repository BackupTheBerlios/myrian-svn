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

import java.util.Collection;
import java.util.Iterator;

/**
 * FlushException. It has an object for which a flush was attempted. If the
 * object is null, the associated flush was for all events. The constructors
 * take a collection of property datas which are responsible for the inability
 * to flush.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/

public class FlushException extends ProtoException {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/FlushException.java#1 $ by $Author: rhs $, $DateTime: 2003/11/09 14:41:17 $";

    private final Object m_obj;

    private static String msg(Object obj, Collection pds) {
        StringBuffer sb = new StringBuffer();
        if (obj == null) {
            sb.append("Unable to send all events to database");
        } else {
            sb.append("Unable to send all events to database for object ");
            sb.append(obj);
        }
        sb.append(" because these required properties are null:");

        for (Iterator it = pds.iterator(); it.hasNext(); ) {
            PropertyData pd = (PropertyData) it.next();
            sb.append("\n ");
            sb.append(pd.getObjectData().getObject() + "."
                      + pd.getProperty().getName());
        }

        return sb.toString();
    }

    FlushException(Object obj, Collection pds) {
	super(msg(obj, pds), false);
        m_obj = obj;

        if (Session.LOG.isInfoEnabled()) {
            Session.LOG.info("Unable to send all events to database.", this);
        }
    }

    FlushException(Collection pds) {
        this(null, pds);
    }

    public Object getObject() { return m_obj; }

}
