package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * JoinKeyNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class JoinKeyNode extends KeyNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/JoinKeyNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private TypeNode m_type;
    private KeyNode m_left;
    private KeyNode m_right;

    JoinKeyNode(TypeNode type, KeyNode left, KeyNode right) {
        m_type = type;
        m_left = left;
        m_right = right;
        add(m_type);
        add(m_left);
        add(m_right);
    }

    void updateKeys() {
        if (m_type.type == null) { return; }
        for (Iterator it = m_left.keys().iterator(); it.hasNext(); ) {
            Collection key = (Collection) it.next();
            updateKeys(key, m_right.keys());
        }
    }

    private void updateKeys(Collection key, Collection keys) {
        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            Collection k = (Collection) it.next();
            HashSet combined = new HashSet(key);
            combined.addAll(k);
            translateKey(combined);
        }
    }

    private void translateKey(Collection key) {
        HashSet translated = new HashSet();
        for (Iterator it = key.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            translated.add(m_type.type.getProperty(prop.getName()));
        }
        add(translated);
    }

}
