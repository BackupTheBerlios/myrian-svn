package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.metadata.Static;

import java.io.*;
import java.util.*;

/**
 * Get
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #13 $ $Date: 2004/02/21 $
 **/

public class Get extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Get.java#13 $ by $Author: rhs $, $DateTime: 2004/02/21 18:22:56 $";

    private Expression m_expr;
    private String m_name;

    public Get(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    void frame(Generator gen) {
        m_expr.frame(gen);
        QFrame expr = gen.getFrame(m_expr);
        QFrame frame = frame(gen, expr, m_name, this);
        frame.addChild(0, expr);
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    static QFrame frame(Generator gen, QFrame expr, String name,
                        Expression result) {
        ObjectType type = expr.getType();
        Property prop = type.getProperty(name);
        if (prop == null) {
            throw new IllegalStateException("no such property: " + name);
        }
        QFrame frame = gen.frame(result, prop.getType());

        // Handle qualii
        if (prop.getRoot() != null) {
            Mapping m = Code.getMapping(prop);
            if (m instanceof Qualias) {
                Qualias q = (Qualias) m;
                String query = q.getQuery();
                OQLParser p = new OQLParser(new StringReader(query));
                // XXX: namespace
                Expression e;
                try {
                    e = p.expression();
                } catch (ParseException pe) {
                    throw new IllegalStateException(pe.getMessage());
                }
                This ths = new This(expr);
                ths.frame(gen);
                gen.push(expr);
                gen.push(gen.getFrame(ths));
                try {
                    e.frame(gen);
                    QFrame qualias = gen.getFrame(e);
                    frame.addChild(qualias);
                    frame.setValues(qualias.getValues());
                    return frame;
                } finally {
                    gen.pop();
                    gen.pop();
                }
            }
        }

        Collection props = Code.properties(prop.getContainer());
        if (!props.contains(prop)) {
            String[] columns = Code.columns(prop, null);
            String table = Code.table(prop);
            if (table == null) {
                QFrame stframe = ((QValue) expr.getValues().get(0)).getFrame();
                List values = new ArrayList();
                for (int i = 0; i < columns.length; i++) {
                    values.add(new QValue(stframe, columns[i]));
                }
                frame.setValues(values);
            } else {
                frame.setValues(columns);
                frame.setTable(table);
                PropertyCondition cond =
                    new PropertyCondition(expr, prop, frame);
                cond.frame(gen);
                frame.setCondition(cond);
            }
        } else {
            int lower = 0;
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property p = (Property) it.next();
                if (p.equals(prop)) {
                    break;
                }
                lower += Code.span(p.getType());
            }
            List values = expr.getValues();
            int upper = lower + Code.span(prop.getType());
            frame.setValues(values.subList(lower, upper));
        }

        return frame;
    }

    private static class PropertyCondition extends Condition {

        private QFrame m_expr;
        private Property m_property;
        private QFrame m_frame;
        private List m_conditions = null;

        PropertyCondition(QFrame expr, Property property, QFrame frame) {
            m_expr = expr;
            m_property = property;
            m_frame = frame;
            conditions();
        }

        void frame(Generator gen) {
            if (m_conditions == null) { return; }

            List values = m_expr.getValues();
            for (int i = 0; i < values.size(); i++) {
                gen.addEquality(this, (QValue) values.get(i),
                                (QValue) m_conditions.get(i));
            }
        }

        String emit(Generator gen) {
            if (m_conditions == null) {
                return emitStatic();
            }

            List values = m_expr.getValues();

            if (values.size() != m_conditions.size()) {
                throw new IllegalStateException
                    ("values doesn't match conditions: " + values + ", " +
                     m_conditions);
            }

            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < values.size(); i++) {
                buf.append(m_conditions.get(i));
                buf.append(" = ");
                buf.append(values.get(i));
                if (i < values.size() - 1) {
                    buf.append(" and ");
                }
            }

            return buf.toString();
        }

        String summary() {
            return toString();
        }

        private String emitStatic() {
            Mapping m = Code.getMapping(m_property);
            List values = m_expr.getValues();
            final String[] key = new String[values.size()];
            for (int i = 0; i < key.length; i++) {
                key[i] = "" + values.get(i);
            }

            StringBuffer buf = new StringBuffer();
            buf.append("exists(select 1 from (");
            Code.bind
                (m.getRetrieve().getSQL(),
                 Code.map
                 (Code.paths(m_property.getContainer(), null), key), buf);
            buf.append(") sg where ");
            Path[] paths = Code.paths
                (m_property.getType(), Path.get(m_property.getName()));
            Code.equals
                (Code.concat("sg.", Code.columns(paths, m.getRetrieve())),
                 Code.columns(m_property.getType(), m_frame.alias()), buf);
            buf.append(")");
            return buf.toString();
        }

        private void conditions() {
            Mapping m = Code.getMapping(m_property);
            if (m.getRetrieve() != null) {
                return;
            }

            m.dispatch(new Mapping.Switch() {
                public void onValue(Value v) {
                    conditions(v.getTable().getPrimaryKey());
                }
                public void onJoinTo(JoinTo j) {
                    conditions(j.getTable().getPrimaryKey());
                }
                public void onJoinFrom(JoinFrom j) {
                    conditions(j.getKey());
                }
                public void onJoinThrough(JoinThrough jt) {
                    conditions(jt.getFrom());
                }
                public void onStatic(Static s) {}
            });
        }

        private void conditions(Constraint c) {
            String[] columns = Code.columns(c, null);
            m_conditions = new ArrayList();
            for (int i = 0; i < columns.length; i++) {
                m_conditions.add(new QValue(m_frame, columns[i]));
            }
        }

        public String toString() {
            return emit((Generator) null);
        }

    }

    private static class This extends Expression {

        private QFrame m_frame;

        This(QFrame frame) {
            m_frame = frame;
        }

        void frame(Generator gen) {
            QFrame ths =
                gen.frame(this, Define.define("this", m_frame.getType()));
            ths.setValues(m_frame.getValues());
        }

        String emit(Generator gen) {
            return gen.getFrame(this).emit();
        }

        String summary() {
            return "this";
        }

    }

    public String toString() {
        if (m_expr instanceof Condition) {
            return "(" + m_expr + ")." + m_name;
        } else {
            return m_expr + "." + m_name;
        }
    }

    String summary() { return "get " + m_name; }

}
