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
package com.redhat.persistence.pdl;

import com.redhat.persistence.pdl.nodes.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * ErrorReport
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class ErrorReport {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/ErrorReport.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private ArrayList m_messages = new ArrayList();
    private boolean m_fatal = false;

    public void warn(Node node, String message) {
        m_messages.add(node.getLocation() + " [warning]: " + message);
    }

    public void fatal(Node node, String message) {
        m_fatal = true;
        m_messages.add(node.getLocation() + " [error]: " + message);
    }

    public Collection getMessages() {
        return m_messages;
    }

    public void check() {
        if (m_messages.size() == 0) {
            return;
        }

        StringBuffer buf = new StringBuffer();
        for (Iterator it = getMessages().iterator(); it.hasNext(); ) {
            buf.append(it.next() + "\n");
        }

        m_messages.clear();

        if (m_fatal) {
            throw new Error(buf.toString());
        } else {
            System.err.println(buf.toString());
        }
    }

}
