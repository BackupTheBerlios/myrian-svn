package com.redhat.persistence.oql;

import java.util.*;

/**
 * FilterKeyNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class FilterKeyNode extends KeyNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/FilterKeyNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private KeyNode m_keys;
    private Frame m_frame;
    private ConstraintNode m_constrained;

    FilterKeyNode(KeyNode keys, Frame frame, ConstraintNode constrained) {
        m_keys = keys;
        m_frame = frame;
        m_constrained = constrained;
        add(m_keys);
        add(m_constrained);
    }

    void updateKeys() {
        Set constrained = new HashSet();
        for (Iterator it = m_constrained.expressions().iterator();
             it.hasNext(); ) {
            Expression e = (Expression) it.next();
            Pane p = m_frame.getPane(e);
            constrained.addAll(p.injection.properties);
            add(p.injection);
        }

        addAll(m_keys);

        Set keys = new HashSet(keys());
        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            Collection key = (Collection) it.next();
            Set k = new HashSet(key);
            k.removeAll(constrained);
            add(k);
        }
    }

}
