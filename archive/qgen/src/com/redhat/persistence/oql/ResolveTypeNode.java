package com.redhat.persistence.oql;

/**
 * ResolveTypeNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class ResolveTypeNode extends TypeNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/ResolveTypeNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    Integer correlation;

    private Frame m_frame;
    private String m_name;

    ResolveTypeNode(Frame frame, String name) {
        m_frame = frame;
        m_name = name;

        Frame f = m_frame;
        while (f != null) {
            add(f.type);
            f = f.parent;
        }
    }

    void updateType() {
        int count = 0;
        Frame frame = m_frame;
        while (frame != null) {
            TypeNode node = frame.type;
            if (node.type == null) { break; }
            if (node.type.hasProperty(m_name)) {
                type = node.type;
                correlation = new Integer(count);
                break;
            }
            count++;
            frame = frame.parent;
        }
    }

}
