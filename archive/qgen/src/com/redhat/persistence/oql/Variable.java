package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Variable
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public class Variable extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Variable.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private String m_name;

    Variable(String name) {
        m_name = name;
    }

    void graph(Pane pane) {
        final ResolveTypeNode type = new ResolveTypeNode(pane.frame, m_name);
        pane.type = new GetTypeNode(type, m_name);
        pane.variables = new VariableNode() {
            { add(type); }
            void updateVariables() {
                variables.put(Variable.this, type.correlation);
            }
        };
        pane.injection = new GetPropertyNode(type, m_name);
        pane.constrained = new ConstraintNode() {void updateConstraints() {}};
        pane.keys = new KeyNode() {
            { add(type); }
            void updateKeys() {
                Property prop = type.type.getProperty(m_name);
                if (prop.isCollection()) {
                    addAll(getKeys(prop.getType()));
                } else {
                    add(Collections.EMPTY_LIST);
                }
            }
        };
    }

    public String toString() {
        return m_name;
    }

    String summary() { return m_name; }

}
