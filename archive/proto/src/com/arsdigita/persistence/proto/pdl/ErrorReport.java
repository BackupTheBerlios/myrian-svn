package com.arsdigita.persistence.proto.pdl;

import com.arsdigita.persistence.proto.pdl.nodes.Node;
import java.util.*;

/**
 * ErrorReport
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

class ErrorReport {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/ErrorReport.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private ArrayList m_messages = new ArrayList();

    public void fatal(Node node, String message) {
        m_messages.add(node.getLocation() + ": " + message);
    }

    public Collection getMessages() {
        return m_messages;
    }

}
