package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Frame
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/01/19 $
 **/

class Frame {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Frame.java#3 $ by $Author: rhs $, $DateTime: 2004/01/19 14:43:24 $";

    Frame parent;
    TypeNode type;

    private Map m_panes = new HashMap();
    private List m_dumpers = new ArrayList();
    private Pane m_parent;

    Frame(Frame parent, TypeNode type) {
        this.parent = parent;
        this.type = type;

        if (this.parent != null) {
            this.parent.m_dumpers.add(this);
            m_parent = this.parent.m_parent;
        }
    }

    Pane add(Expression expression) {
        Pane pane = new Pane(this, expression, m_parent);
        m_dumpers.add(pane);
        m_panes.put(expression, pane);
        return pane;
    }

    Pane getPane(Expression expr) {
        return (Pane) m_panes.get(expr);
    }

    Pane graph(Expression expr) {
        Pane result = add(expr);
        Pane previous = m_parent;
        m_parent = result;
        try {
            expr.graph(result);
        } finally {
            m_parent = previous;
        }
        return result;
    }

    void dump(Indentor out) {
        for (Iterator it = m_dumpers.iterator(); it.hasNext(); ) {
            Object o = it.next();
            if (o instanceof Pane) {
                ((Pane) o).dump(out);
            } else if (o instanceof Frame) {
                out.level+=2;
                try {
                    ((Frame) o).dump(out);
                } finally {
                    out.level-=2;
                }
            }
        }
    }

    static Frame root(Root root) {
        ObjectType type = new ObjectType(null, "root", null);
        for (Iterator it = root.getObjectTypes().iterator(); it.hasNext(); ) {
            ObjectType to = (ObjectType) it.next();
            if (to.isKeyed()) {
                Expression.addKey(to, to.getKeyProperties());
                addPath(type, Path.get(to.getQualifiedName()), to);
            }
        }

        TypeNode tn = new TypeNode() { void updateType() {} };
        tn.type = type;
        Frame frame = new Frame(null, tn);
        return frame;
    }

    private static void addPath(ObjectType from, Path path, ObjectType to) {
        if (path.getParent() == null) {
            Property prop = from.getProperty(path.getName());
            if (prop != null) { return; }
            from.addProperty
                (new Role(path.getName(), to, false, to.getRoot() != null,
                          true));
        } else {
            Property prop = from.getProperty(path.getParent());
            ObjectType type;
            if (prop == null) {
                type = new ObjectType(null, path.getPath() + " intermediate",
                                      null);
                addPath(from, path.getParent(), type);
            } else {
                type = prop.getType();
            }

            addPath(type, Path.get(path.getName()), to);
        }
    }

}
