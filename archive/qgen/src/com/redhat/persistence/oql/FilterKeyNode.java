package com.redhat.persistence.oql;

import java.util.*;

/**
 * FilterKeyNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/19 $
 **/

class FilterKeyNode extends KeyNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/FilterKeyNode.java#2 $ by $Author: rhs $, $DateTime: 2004/01/19 14:43:24 $";

    private KeyNode m_keys;
    private ConstraintNode m_constrained;

    FilterKeyNode(KeyNode keys, ConstraintNode constrained) {
        m_keys = keys;
        m_constrained = constrained;
        add(m_keys);
        add(m_constrained);
    }

    void updateKeys() {
        Set constrained = new HashSet();
        for (Iterator it = m_constrained.expressions().iterator();
             it.hasNext(); ) {
            Expression e = (Expression) it.next();
            Pane p = m_constrained.getPane(e);
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
