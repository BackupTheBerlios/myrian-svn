package com.redhat.persistence.oql;

import com.redhat.persistence.ProtoException;
import com.redhat.persistence.common.*;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;

import java.io.*;
import java.util.*;

/**
 * Static
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/02/23 $
 **/

public class Static extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Static.java#6 $ by $Author: ashah $, $DateTime: 2004/02/23 11:51:21 $";

    private SQL m_sql;
    private String m_type;
    private String[] m_columns;
    private boolean m_map;
    private Map m_bindings;
    private List m_expressions = new ArrayList();

    public Static(String sql) {
        this(sql, Collections.EMPTY_MAP);
    }

    public Static(String sql, Map bindings) {
        this(parse(sql), null, null, true, bindings);
    }

    Static(SQL sql, String type, String[] columns, boolean map, Map bindings) {
        m_sql = sql;
        m_type = type;
        m_columns = columns;
        m_map = map;
        m_bindings = bindings;

        for(SQLToken t = m_sql.getFirst(); t != null; t = t.getNext()) {
            if (isExpression(t)) {
                String image = t.getImage();
                Expression e;
                if (t.isBind()) {
                    e = bind(image);
                } else if (m_map && t.isPath()) {
                    e = new Choice(new All(image, m_bindings), path(image));
                } else if (!m_map && t.isPath()) {
                    e = new Choice(new All(image, m_bindings), image);
                } else {
                    throw new IllegalStateException
                        ("don't know how to deal with token: " + t);
                }
                m_expressions.add(e);
            }
        }
    }

    private boolean isExpression(SQLToken tok) {
        return tok.isBind() || tok.isPath();
    }

    private static SQL parse(String sql) {
        final List exprs = new ArrayList();
        SQLParser p = new SQLParser(new StringReader(sql));
        try {
            p.sql();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return p.getSQL();
    }

    private Expression bind(String image) {
        return expression(Path.get(image), true);
    }

    private Expression path(String image) {
        return expression(Path.get(image), false);
    }

    private Expression expression(Path path, boolean isBind) {
        if (path.getParent() == null) {
            String name = path.getName();
            if (isBind) {
                String key = name.substring(1);
                if (m_bindings.containsKey(key)) {
                    return new Literal(m_bindings.get(key));
                }

                // XXX: use real subtype
                throw new ProtoException
                    ("no " + key + " in " + m_bindings, false) {};
            } else {
                return new Variable(name);
            }
        } else {
            return new Get
                (expression(path.getParent(), isBind), path.getName());
        }
    }

    void frame(Generator gen) {
        for (Iterator it = m_expressions.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.frame(gen);
        }
        if (m_type != null) {
            QFrame frame = gen.frame(this, gen.getType(m_type));
            frame.setValues(m_columns);
            frame.setTable(this);
        } else if (m_expressions.size() == 1
                   && m_sql.getFirst().equals(m_sql.getLast())) {
            Expression e = (Expression) m_expressions.get(0);
            if (gen.hasFrame(e)) {
                QFrame child = gen.getFrame(e);
                QFrame frame = gen.frame(this, child.getType());
                frame.addChild(child);
                frame.setValues(child.getValues());
            }
        }
    }

    String emit(Generator gen) {
        if (m_type == null && gen.hasFrame(this)) {
            return gen.getFrame(this).emit();
        }

        StringBuffer buf = new StringBuffer();
        int index = 0;
        if (m_type != null) { buf.append("("); }
        for (SQLToken t = m_sql.getFirst(); t != null; t = t.getNext()) {
            if (isExpression(t)) {
                Expression e = (Expression) m_expressions.get(index++);
                buf.append(e.emit(gen));
            } else if (t.isRaw())  {
                // XXX: ignore escapes for now
                String raw = t.getImage();
                buf.append(raw.substring(4, raw.length() - 1));
            } else {
                buf.append(t.getImage());
            }
        }
        if (m_type != null) { buf.append(")"); }
        return buf.toString();
    }

    private class Choice extends Expression {

        private All m_all;
        private Expression m_expression;
        private String m_image;

        Choice(All all, Expression alternative) {
            m_all = all;
            m_expression = alternative;
        }

        Choice(All all, String alternative) {
            m_all = all;
            m_image = alternative;
        }

        void frame(Generator gen) {
            QFrame child = null;
            if (gen.hasType(m_all.getType())) {
                m_all.frame(gen);
                if (gen.hasFrame(m_all)) {
                    child = gen.getFrame(m_all);
                }
            } else if (m_expression != null) {
                m_expression.frame(gen);
                if (gen.hasFrame(m_expression)) {
                    child = gen.getFrame(m_expression);
                }
            }

            if (child != null) {
                QFrame frame = gen.frame(this, child.getType());
                frame.addChild(child);
                frame.setValues(child.getValues());
            }
        }

        String emit(Generator gen) {
            if (gen.hasFrame(this)) {
                return gen.getFrame(this).emit();
            } else if (gen.hasType(m_all.getType())) {
                return m_all.emit(gen);
            } else if (m_expression != null) {
                return m_expression.emit(gen);
            } else {
                return m_image;
            }
        }

        String summary() { return this.toString(); }

    }

    public String toString() {
        return "sql {" + m_sql + "}";
    }

    String summary() {
        return toString();
    }

}
