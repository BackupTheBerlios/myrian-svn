package com.arsdigita.persistence.proto.pdl;

import com.arsdigita.persistence.proto.pdl.nodes.Node;
import java.util.*;

/**
 * ErrorReport
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/05 $
 **/

class ErrorReport {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/ErrorReport.java#2 $ by $Author: rhs $, $DateTime: 2003/02/05 18:34:37 $";

    private ArrayList m_messages = new ArrayList();

    public void warn(Node node, String message) {
        // do nothing right now
    }

    public void fatal(Node node, String message) {
        m_messages.add(node.getLocation() + ": " + message);
    }

    public Collection getMessages() {
        return m_messages;
    }

    public void check() {
        StringBuffer buf = new StringBuffer();
        for (Iterator it = getMessages().iterator(); it.hasNext(); ) {
            buf.append(it.next() + "\n");
        }

        if (buf.length() > 0) {
            throw new Error(buf.toString());
        }
    }

}
