/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 **/

public class FlushException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/FlushException.java#9 $ by $Author: dennis $, $DateTime: 2004/08/16 18:10:38 $";

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
