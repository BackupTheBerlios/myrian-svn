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
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.Event;
import com.redhat.persistence.Signature;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * RDBMSStatement
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class RDBMSStatement {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/RDBMSStatement.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private final String m_text;
    private final ArrayList m_events = new ArrayList();
    private Signature m_sig = null;

    RDBMSStatement(String text) {
        m_text = text;
    }

    public String getText() {
        return m_text;
    }

    void setSignature(Signature sig) {
        m_sig = sig;
    }

    public Signature getSignature() {
        return m_sig;
    }

    void addEvent(Event ev) {
        if (m_events.contains(ev)) {
            throw new IllegalArgumentException
                ("Already contains event: " + ev);
        }

        m_events.add(ev);
    }

    public Collection getEvents() {
        return Collections.unmodifiableCollection(m_events);
    }

}
