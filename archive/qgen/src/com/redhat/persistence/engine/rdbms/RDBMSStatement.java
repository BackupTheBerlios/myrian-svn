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
 * @version $Revision: #2 $ $Date: 2004/02/24 $
 **/

public class RDBMSStatement {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/engine/rdbms/RDBMSStatement.java#2 $ by $Author: ashah $, $DateTime: 2004/02/24 12:49:36 $";

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
