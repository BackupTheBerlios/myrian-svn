package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Variable
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/01/23 $
 **/

public class Variable extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Variable.java#3 $ by $Author: rhs $, $DateTime: 2004/01/23 15:34:30 $";

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

    Code.Frame frame(Code code) {
        Code.Frame parent = code.get(m_name);
        Property prop = parent.type.getProperty(m_name);
        Code.Frame frame = code.frame(prop.getType());
        // XXX: hack for package lookup
        if (parent.type.getQualifiedName().equals("root")) {
            code.addPackage(this);
        } else {
            frame.alias();
            code.setContext(this, parent);
            code.setFrame(this, frame);
        }
        return frame;
    }

    void emit(Code code) {
        // XXX: hack for package lookup
        if (code.isPackage(this)) { return; }

        Code.Frame parent = code.getContext(this);
        Property prop = parent.type.getProperty(m_name);
        Code.Frame frame = code.getFrame(this);

        String[] columns = parent.getColumns(prop);
        code.append("(select ");
        if (columns == null) {
            code.alias(prop, frame.getColumns());
            code.append(" from ");
            code.table(prop);
            code.append(" where ");
            code.condition(prop, parent.getColumns());
        } else {
            code.alias(columns, frame.getColumns());
        }
        code.append(")");
    }

    public String toString() {
        return m_name;
    }

    String summary() { return m_name; }

}
