package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Environment
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

class Environment {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Environment.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    private List m_expressions = new ArrayList();
    private Map m_frames = new HashMap();

    void add(Expression e, Frame parent) {
        if (m_frames.containsKey(e)) {
            throw new IllegalArgumentException
                ("expression already added: " + e);
        }
        m_frames.put(e, new Frame(e, parent));
        m_expressions.add(e);
        e.add(this, parent);
    }

    Frame getFrame(Expression e) {
        return (Frame) m_frames.get(e);
    }

    void analyze() {
        List remaining = new ArrayList(m_expressions.size());
        remaining.addAll(m_expressions);

        int before;
        do {
            before = remaining.size();
            for (Iterator it = remaining.iterator(); it.hasNext(); ) {
                Expression e = (Expression) it.next();
                Frame f = getFrame(e);
                if (f.getType() == null) { e.type(this, f); }
                if (f.getType() != null) { it.remove(); }
            }
        } while (remaining.size() < before);

        for (int i = 0; i < 1000; i++) {
            for (Iterator it = m_expressions.iterator(); it.hasNext(); ) {
                Expression e = (Expression) it.next();
                Frame f = getFrame(e);
                e.count(this, f);
            }
        }
    }

    Property getProperty(Frame frame, String name) {
        if (frame == null) { return null; }
        ObjectType type = frame.getType();
        if (type == null) { return null; }
        if (type.hasProperty(name)) {
            return type.getProperty(name);
        } else {
            return getProperty(frame.getParent(), name);
        }
    }

    int getCorrelation(Frame frame, String name) {
        if (frame == null) { return 1; }
        ObjectType type = frame.getType();
        if (type == null) { return Integer.MAX_VALUE; }
        if (type.hasProperty(name)) {
            return 1;
        } else {
            int c = getCorrelation(frame.getParent(), name);
            if (c == Integer.MAX_VALUE) { return c; }
            return 1 + c;
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        dump(buf, null);
        return buf.toString();
    }

    void dump(StringBuffer buf, Frame parent) {
        for (Iterator it = m_expressions.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            Frame f = getFrame(e);
            if (f.getParent() == parent) {
                buf.append(fmt(f));
                buf.append("\n");
                dump(buf, f);
            }
        }
    }

    private String id(Frame frame) {
        if (frame == null) { return null; }
        return "" + m_expressions.indexOf(frame.getExpression());
    }

    private String fmt(Frame frame) {
        String indent = "";
        Frame f = frame.getParent();
        while (f != null) {
            indent += "  ";
            f = f.getParent();
        }

        int cmax = frame.getCorrelationMax();
        int cmin = frame.getCorrelationMin();

        return indent + "frame " + id(frame) + ":" +
            "\n  " + indent + "parent = " + id(frame.getParent()) +
            "\n  " + indent + "expr = " + frame.getExpression() +
            "\n  " + indent + "type = " + fmt(indent, frame.getType()) +
            "\n  " + indent + "mult = " + (frame.isNullable() ? "0" : "1") +
            ".." + (frame.isCollection() ? "n" : "1") +
            "\n  " + indent + "set = " + frame.isSet() +
            "\n  " + indent + "correlation = " +
            (cmin == Integer.MIN_VALUE ? "null" : "" + cmin) + ", " +
            (cmax == Integer.MAX_VALUE ? "null" : "" + cmax) +
            "\n  " + indent + "constrained = " + frame.getConstrained() +
            "\n  " + indent + "injection = " + frame.getInjection() +
            "\n  " + indent + "keys = " + frame.getKeys() +
            "\n";
    }

    private static String fmt(String indent, ObjectType type) {
        if (type == null) { return null; }
        StringBuffer buf = new StringBuffer();
        buf.append(type.getQualifiedName());
        buf.append("(");
        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            buf.append(prop.getName());
            if (it.hasNext()) {
                buf.append(", ");
            }
        }
        buf.append(")\n    ");
        buf.append(indent);
        buf.append("unique");
        buf.append(Expression.getKeys(type));
        return buf.toString();
    }

}
