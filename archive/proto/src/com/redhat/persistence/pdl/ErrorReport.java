package com.redhat.persistence.pdl;

import com.redhat.persistence.pdl.nodes.Node;
import java.util.*;

/**
 * ErrorReport
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

class ErrorReport {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/ErrorReport.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

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
