package com.redhat.persistence.oql;

import java.util.*;

/**
 * Pane
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class Pane {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Pane.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    Frame frame;
    Expression expression;
    Pane parent;
    TypeNode type;
    VariableNode variables;
    ConstraintNode constrained;
    PropertyNode injection;
    KeyNode keys;

    private List m_children = new ArrayList();

    Pane(Frame frame, Expression expression, Pane parent) {
        this.frame = frame;
        this.expression = expression;
        this.parent = parent;

        if (this.parent != null) {
            this.parent.m_children.add(this);
        }
    }

    Collection children() {
        return Collections.unmodifiableList(m_children);
    }

    private int depth() {
        if (parent == null) { return 0; }
        if (parent.frame != frame) { return 0; }
        return parent.depth() + 1;
    }

    void dump(Indentor out) {
        int depth = depth();
        out.level += depth;
        try {
            out.println(expression.toString() + ":");
            out.level++;
            try {
                out.print("T = " + type);
                out.print(", V = " + variables);
                out.print(", I = " + injection);
                out.print(", C = " + constrained);
                out.println(", K = " + keys);
            } finally {
                out.level--;
            }
        } finally {
            out.level -= depth;
        }
    }

}
