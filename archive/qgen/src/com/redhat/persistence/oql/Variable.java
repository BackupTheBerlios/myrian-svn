package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Variable
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/02/21 $
 **/

public class Variable extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Variable.java#8 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    private String m_name;

    public Variable(String name) {
        m_name = name;
    }

    void frame(Generator gen) {
        QFrame parent = gen.resolve(m_name);
        Get.frame(gen, parent, m_name, this);
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
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
        if (parent == null) {
            throw new IllegalStateException
                ("no such variable: " + m_name + "\n" + code.getTrace());
        }
        Property prop = parent.type.getProperty(m_name);

        Code.Frame frame;
        if (code.isQualias(prop)) {
            frame = code.frame(this, parent, prop);
        } else {
            frame = code.frame(prop.getType());
            String[] columns = parent.getColumns(prop);
            if (columns == null) {
                String alias = frame.alias(prop);
                code.setAlias(this, alias);
                code.setTable(alias, code.table(prop));
                frame.condition(prop, alias, parent.getColumns());
            } else {
                frame.setColumns(columns);
            }
        }
        code.setContext(this, parent);
        code.setFrame(this, frame);
        return frame;
    }

    void opt(Code code) {
        Code.Frame parent = code.getContext(this);
        Property prop = parent.type.getProperty(m_name);
        if (code.isQualias(prop)) {
            code.opt(this);
        }
    }

    void emit(Code code) {
        Code.Frame parent = code.getContext(this);
        Property prop = parent.type.getProperty(m_name);
        Code.Frame frame = code.getFrame(this);
        String alias = code.getAlias(this);

        if (code.isQualias(prop)) {
            code.emit(this);
        } else {
            String join = frame.join();
            if (join != null) {
                code.append(join);
            } else {
                code.append("(select 1) " + code.var("d"));
            }

            /*String[] columns = parent.getColumns(prop);
            if (columns == null) {
                code.append(code.table(prop));
                code.append(" ");
                code.append(alias);
                if (prop.isNullable() && !prop.isCollection()) {
                    code.append(" right");
                }
                code.append(" join (select 1) " + code.var("d") + " on ");
                code.condition(prop, alias, parent.getColumns());
            } else {
                code.append("(select 2) " + code.var("d"));
                }*/
        }
    }

    public String toString() {
        return m_name;
    }

    String summary() { return m_name; }

}
