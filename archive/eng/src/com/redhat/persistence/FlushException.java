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

import java.util.Collection;
import java.util.Iterator;

/**
 * FlushException. It has an object for which a flush was attempted. If the
 * object is null, the associated flush was for all events. The constructors
 * take a collection of property datas which are responsible for the inability
 * to flush.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class FlushException extends ProtoException {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/FlushException.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private final Object m_obj;

    private static String msg(Object obj, Collection violations) {
        StringBuffer sb = new StringBuffer();
        if (obj == null) {
            sb.append("Unable to send all events to database");
        } else {
            sb.append("Unable to send all events to database for object ");
            sb.append(obj);
        }
        sb.append(":");

        for (Iterator it = violations.iterator(); it.hasNext(); ) {
            Violation v = (Violation) it.next();
            sb.append("\n  ");
            sb.append(v.getViolationMessage());
        }

        return sb.toString();
    }

    FlushException(Object obj, Collection violations) {
	super(msg(obj, violations), false);
        m_obj = obj;

        if (Session.LOG.isInfoEnabled()) {
            Session.LOG.info("Unable to send all events to database.", this);
        }
    }

    FlushException(Collection violations) {
        this(null, violations);
    }

    public Object getObject() { return m_obj; }

}
