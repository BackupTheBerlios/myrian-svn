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
 * @version $Revision: #3 $ $Date: 2004/03/24 $
 **/

public class Static extends Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Static.java#3 $ by $Author: ashah $, $DateTime: 2004/03/24 13:21:25 $";

    private SQL m_sql;
    private String[] m_columns;
    private boolean m_map;
    private Map m_bindings;
    private List m_expressions = new ArrayList();

    private static final Collection s_functions = new HashSet();
    static {
        String[] functions = {
            /* sql standard functions supported by both oracle and postgres.
             * there is an added caveat that the function uses normal function
             * syntax and not keywords as arguments (e.g. trim(leading 'a'
             * from str), substring('teststr' from 3 for 2))
             */
            "current_date", "current_timestamp",
            "upper", "lower",
            "trim", // only trim(str) syntax is allowed
            // postgres supported oracle-isms
            "substr", "length", "nvl"
        };
        for (int i = 0; i < functions.length; i++) {
            s_functions.add(functions[i]);
        }
    }

    private static final boolean isAllowedFunction(String s) {
        return s_functions.contains(s);
    }

    public Static(String sql) {
        this(sql, Collections.EMPTY_MAP);
    }

    public Static(String sql, Map bindings) {
        this(parse(sql), null, true, bindings);
    }

    public Static(SQL sql, String[] columns, boolean map, Map bindings) {
        m_sql = sql;
        m_columns = columns;
        m_map = map;
        m_bindings = bindings;

        int size = size(m_sql);

        for(SQLToken t = m_sql.getFirst(); t != null; t = t.getNext()) {
            if (isExpression(t)) {
                String image = t.getImage();
                Expression e;
                if (t.isBind()) {
                    e = bind(image);
                } else if (t.isPath()) {
                    All all = new All(image, m_bindings, size != 1);
                    if (isAllowedFunction(image) || !m_map) {
                        e = new Choice(all, image);
                    } else {
                        e = new Choice(all, path(image));
                    }
                } else {
                    throw new IllegalStateException
                        ("don't know how to deal with token: " + t);
                }
                m_expressions.add(e);
            }
        }
    }

    private static boolean isExpression(SQLToken tok) {
        return tok.isBind() || tok.isPath();
    }

    private static ThreadLocal s_parsers = new ThreadLocal() {
        protected Object initialValue() {
            return new SQLParser(new StringReader(""));
        }
    };

    private static SQL parse(String sql) {
        SQLParser p = (SQLParser) s_parsers.get();
        p.initialize(new StringReader(sql));

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

    private static int size(SQL sql) {
        int size = 0;
        for (SQLToken t = sql.getFirst(); t != null; t = t.getNext()) {
            String image = t.getImage();
            for (int i = 0; i < image.length(); i++) {
                if (!Character.isWhitespace(image.charAt(i))) {
                    size++;
                    break;
                }
            }
        }
        return size;
    }

    protected ObjectType getType() { return null; }
    protected boolean hasType() { return false; }

    void frame(Generator gen) {
        boolean bool = gen.isBoolean(this) && m_expressions.size() == 1;
        for (Iterator it = m_expressions.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            if (bool) { gen.addBoolean(e); }
            e.frame(gen);
            gen.addUses(this, gen.getUses(e));
        }
        if (hasType()) {
            ObjectType type = getType();
            QFrame frame = gen.frame(this, type);
            frame.setValues(m_columns);
            frame.setTable(this);
        } else if (!gen.isBoolean(this) && m_expressions.size() == 1
                   && size(m_sql) == 1) {
            Expression e = (Expression) m_expressions.get(0);
            if (gen.hasFrame(e)) {
                QFrame child = gen.getFrame(e);
                QFrame frame = gen.frame(this, child.getType());
                frame.addChild(child);
                frame.setValues(child.getValues());
                frame.setMappings(child.getMappings());
            }
        }
    }

    Code emit(Generator gen) {
        if (!hasType() && gen.hasFrame(this)) {
            return gen.getFrame(this).emit();
        }

        Code result = new Code();
        int index = 0;
        if (hasType()) { result = result.add("("); }
        for (SQLToken t = m_sql.getFirst(); t != null; t = t.getNext()) {
            if (isExpression(t)) {
                Expression e = (Expression) m_expressions.get(index++);
                result = result.add(e.emit(gen));
            } else if (t.isRaw())  {
                // XXX: ignore escapes for now
                String raw = t.getImage();
                result = result.add(raw.substring(4, raw.length() - 1));
            } else {
                result = result.add(t.getImage());
            }
        }
        if (hasType()) { result = result.add(")"); }
        return result;
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
                if (gen.isBoolean(this)) { gen.addBoolean(m_all); }
                m_all.frame(gen);
                if (gen.hasFrame(m_all)) {
                    child = gen.getFrame(m_all);
                    gen.addUses(this, gen.getUses(m_all));
                }
            } else if (m_expression != null) {
                if (gen.isBoolean(this)) { gen.addBoolean(m_expression); }
                m_expression.frame(gen);
                if (gen.hasFrame(m_expression)) {
                    child = gen.getFrame(m_expression);
                }
                gen.addUses(this, gen.getUses(m_expression));
            }

            if (child != null) {
                QFrame frame = gen.frame(this, child.getType());
                frame.addChild(child);
                frame.setValues(child.getValues());
                frame.setMappings(child.getMappings());
            }
        }

        Code emit(Generator gen) {
            if (gen.hasFrame(this)) {
                return gen.getFrame(this).emit();
            } else if (gen.hasType(m_all.getType())) {
                return m_all.emit(gen);
            } else if (m_expression != null) {
                return m_expression.emit(gen);
            } else {
                return new Code(m_image);
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
