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
 * @version $Revision: #15 $ $Date: 2004/02/27 $
 **/

public class Get extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Get.java#15 $ by $Author: rhs $, $DateTime: 2004/02/27 16:35:42 $";

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
        Property prop = expr.getType().getProperty(m_name);
        if (!prop.isCollection()) {
            List children = frame.getChildren();
            for (int i = 1; i < children.size(); i++) {
                QFrame child = (QFrame) children.get(i);
                child.setOuter(true);
            }
        }
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
                    gen.addUses(result, frame.getValues());
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
                TableAll tall = new TableAll
                    (table, columns, prop.getType().getQualifiedName());
                tall.frame(gen);
                QFrame tbl = gen.getFrame(tall);
                PropertyCondition cond =
                    new PropertyCondition(expr, prop, tbl);
                cond.frame(gen);
                tbl.setCondition(cond);
                frame.addChild(tbl);
                frame.setValues(tbl.getValues());
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

        gen.addUses(result, frame.getValues());

        return frame;
    }

    private static class PropertyCondition extends Condition {

        private QFrame m_expr;
        private Property m_property;
        private QFrame m_frame;
        private This m_this;
        private Key m_key = null;

        PropertyCondition(QFrame expr, Property property, QFrame frame) {
            m_expr = expr;
            m_property = property;
            m_frame = frame;
            m_this = new This(m_expr);
            conditions();
        }

        void frame(Generator gen) {
            if (m_key == null) {
                gen.addUses(this, m_expr.getValues());
                gen.addUses(this, m_frame.getValues());
                return;
            } else {
                m_this.frame(gen);
                m_key.frame(gen);
                QFrame ths = gen.getFrame(m_this);
                QFrame key = gen.getFrame(m_key);
                gen.addUses(this, ths.getValues());
                gen.addUses(this, key.getValues());
                Equals.equate(gen, this, ths, key);
            }
        }

        String emit(Generator gen) {
            if (m_key == null) {
                return emitStatic();
            } else {
                return Equals.emit(gen, m_this, m_key);
            }
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
            m_key = new Key(m_frame, columns);
        }

        public String toString() {
            return emit(m_frame.getGenerator());
        }

    }

    private static class Key extends Expression {

        private QFrame m_frame;
        private String[] m_columns;

        Key(QFrame frame, String[] columns) {
            m_frame = frame;
            m_columns = columns;
        }

        void frame(Generator gen) {
            QFrame frame = gen.frame(this, null);
            List values = new ArrayList();
            for (int i = 0; i < m_columns.length; i++) {
                values.add(new QValue(m_frame, m_columns[i]));
            }
            frame.setValues(values);
        }

        String emit(Generator gen) {
            return gen.getFrame(this).emit();
        }

        String summary() {
            return toString();
        }

        public String toString() {
            return m_frame.alias() + Arrays.asList(m_columns);
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

    private static class TableAll extends Expression {

        private String m_table;
        private String[] m_columns;
        private String m_type;

        TableAll(String table, String[] columns, String type) {
            m_table = table;
            m_columns = columns;
            m_type = type;
        }

        void frame(Generator gen) {
            QFrame frame = gen.frame(this, gen.getType(m_type));
            frame.setValues(m_columns);
            frame.setTable(m_table);
        }

        String emit(Generator gen) {
            return gen.getFrame(this).emit();
        }

        String summary() {
            return "tall: " + m_table;
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
