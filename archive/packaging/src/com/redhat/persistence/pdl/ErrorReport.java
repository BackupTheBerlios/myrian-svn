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

package com.redhat.persistence.pdl;

import com.redhat.persistence.pdl.nodes.Node;
import java.util.*;

/**
 * ErrorReport
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

class ErrorReport {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/ErrorReport.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

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
