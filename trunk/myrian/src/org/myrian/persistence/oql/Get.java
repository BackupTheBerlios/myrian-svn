/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.oql;

import org.myrian.persistence.common.*;
import org.myrian.persistence.metadata.*;
import org.myrian.persistence.metadata.Static;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Get
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class Get extends Expression {


    private static final Logger s_log = Logger.getLogger(Get.class);

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
        // prop is null for container mappings
        if (prop == null || !prop.isCollection()) {
            List children = frame.getChildren();
            for (int i = 1; i < children.size(); i++) {
                QFrame child = (QFrame) children.get(i);
                child.setOuter(true);
            }
        }
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_expr.hash(gen);
        gen.hash(m_name);
        gen.hash(getClass());
    }

    private static ThreadLocal s_parsers = new ThreadLocal() {
        protected Object initialValue() {
            return new OQLParser(new StringReader(""));
        }
    };

    static QFrame frame(Generator gen, QFrame expr, String name,
                        Expression result) {
        ObjectMap map = expr.getMap();
        ObjectType type = map.getObjectType();
        Mapping mapping = map.getMapping(Path.get(name));
        if (mapping == null) {
            throw new IllegalStateException
                ("no such mapping '" + name + "' in expression " +
                 expr.getExpression());
        }
        QFrame frame = gen.frame(result, mapping.getMap());

        // Handle qualii
        if (mapping instanceof Qualias) {
            Qualias q = (Qualias) mapping;
            String query = q.getQuery();
            OQLParser p = (OQLParser) s_parsers.get();
            p.ReInit(new StringReader(query));
            // XXX: namespace
            Expression e;
            try {
                e = p.expression();
            } catch (ParseException pe) {
                throw new IllegalStateException(pe.getMessage());
            }
            gen.level++;
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
                gen.level--;
                gen.pop();
                gen.pop();
            }
        }

        if (expr.hasMappings()) {
            Path path = mapping.getPath();
            for (Iterator it = expr.getMappings().entrySet().iterator();
                 it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                Path key = (Path) me.getKey();
                String value = (String) me.getValue();
                if (path.isAncestor(key)) {
                    frame.addMapping(Path.relative(path, key), value);
                }
            }
        }

        Collection keys = map.getKeyMappings();
        if (mapping.isNested() && mapping.isCompound() &&
            ((mapping instanceof Static) || mapping instanceof Nested)) {
            frame.setValues(expr.getValues());
        } else if (!keys.contains(mapping)) {
            String[] columns = null;
            String table = null;
            boolean mappings = false;
            if (expr.hasMappings()) {
                columns = Code.columns
                    (mapping.getMap().getObjectType(), expr,
                     mapping.getPath());
                if (columns != null) {
                    mappings = true;
                }
            }
            if (columns == null) {
                columns = Code.columns(mapping, (String) null);
                table = Code.table(mapping);
            }

            if (mappings) {
                QFrame stframe = ((QValue) expr.getValues().get(0)).getFrame();
                List values = new ArrayList();
                for (int i = 0; i < columns.length; i++) {
                    values.add(stframe.getValue(columns[i]));
                }
                frame.setValues(values);
            } else {
                TableAll tall = new TableAll
                    (table, columns, mapping.getMap());
                tall.frame(gen);
                QFrame tbl = gen.getFrame(tall);
                MappingCondition cond =
                    new MappingCondition(expr, mapping, tbl);
                cond.frame(gen);
                tbl.setCondition(cond);
                frame.addChild(tbl);
                frame.setValues(tbl.getValues());
            }
        } else {
            int lower = 0;
            for (Iterator it = keys.iterator(); it.hasNext(); ) {
                Mapping m = (Mapping) it.next();
                if (m.equals(mapping)) {
                    break;
                }
                lower += Code.span(m.getMap());
            }
            List values = expr.getValues();
            int upper = lower + Code.span(mapping.getMap());
            frame.setValues(values.subList(lower, upper));
        }

        gen.addUses(result, frame.getValues());

        return frame;
    }

    private static class MappingCondition extends Condition {

        private QFrame m_expr;
        private Mapping m_mapping;
        private QFrame m_frame;
        private This m_this;
        private Key m_key = null;

        MappingCondition(QFrame expr, Mapping mapping, QFrame frame) {
            m_expr = expr;
            m_mapping = mapping;
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

        Code emit(Generator gen) {
            if (m_key == null) {
                return emitStatic();
            } else {
                return Equals.emit(gen, m_this, m_key);
            }
        }

        void hash(Generator gen) {
            throw new UnsupportedOperationException();
        }

        String summary() {
            return toString();
        }

        private Code emitStatic() {
            List values = m_expr.getValues();
            Code[] from = new Code[values.size()];
            for (int i = 0; i < from.length; i++) {
                QValue qv = (QValue) values.get(i);
                from[i] =  qv.emit();
            }

            Path[] paths = Code.paths
                (m_mapping.getMap().getObjectType(), m_mapping.getPath());
            String[] cols = Code.columns(paths, m_mapping.getRetrieve());
            Map bindings = Code.map
                (Code.paths(m_mapping.getObjectMap().getObjectType(), null),
                 from);
            String[] to = Code.columns(m_mapping.getMap(), m_frame.alias());

            Code result = new Code("(");
            for (int i = 0; i < to.length; i++) {
                result = result.add(to[i]);
                if (i < to.length - 1) {
                    result = result.add(", ");
                }
            }
            result = result.add(") in (");
            result = in(m_mapping.getRetrieve().getSQL(), cols, bindings,
                        result);
            result = result.add(")");
            return result;
        }

        static boolean is(SQLToken t, String image) {
            return t.getImage().trim().equalsIgnoreCase(image);
        }

        static Code in(SQL sql, String[] columns, Map values, Code buf) {
            SQLToken t;
            boolean select = false;
            int depth = 0;
            Map selections = new HashMap();
            StringBuffer selection = new StringBuffer();
            String prefix = null;
            for (t = sql.getFirst(); t != null; t = t.getNext()) {
                if (depth == 0 && is(t, "select")) {
                    select = true;
                } else if (depth == 0 && select &&
                           (is(t, ",") || is(t, "from"))) {
                    String sel = selection.toString();
                    String match = null;
                    for (int i = 0; i < columns.length; i++) {
                        if (sel.trim().endsWith(columns[i])) {
                            if (match == null) {
                                match = columns[i];
                            } else if (columns[i].length() > match.length()) {
                                match = columns[i];
                            }
                            selections.put(match, sel);
                        }
                    }
                    if (match == null) {
                        String trimmed = sel.trim();
                        if (trimmed.equals("*")) {
                            // do nothing
                        } else if (trimmed.endsWith("*")) {
                            if (prefix != null) {
                                throw new IllegalStateException
                                    ("multiple prefixes: " + sql);
                            }
                            prefix =
                                trimmed.substring(0, trimmed.indexOf('.'));
                        }
                    }
                    if (is(t, "from")) {
                        break;
                    }
                    selection = new StringBuffer();
                } else {
                    if (is(t, "(")) {
                        depth++;
                    } else if (is(t, ")")) {
                        depth--;
                    }
                    selection.append(t.getImage());
                }
            }

            buf = buf.add("select ");
            for (int i = 0; i < columns.length; i++) {
                String sel = (String) selections.get(columns[i]);
                if (sel == null) {
                    if (prefix != null) {
                        buf = buf.add(prefix).add(".");
                    }
                    buf = buf.add(columns[i]);
                } else {
                    buf = buf.add(sel);
                }
                if (i < columns.length - 1) {
                    buf = buf.add(", ");
                }
            }
            buf = buf.add(" ");

            for (; t != null; t = t.getNext()) {
                if (t.isBind()) {
                    Path key = Path.get(t.getImage().substring(1));
                    Code value = (Code) values.get(key);
                    if (value == null) {
                        throw new IllegalStateException
                            ("no value for: " + key + " in " + values);
                    }
                    buf = buf.add(value);
                } else {
                    buf = buf.add(t.getImage());
                }
            }

            return buf;
        }

        private void conditions() {
            if (m_mapping.getRetrieve() != null) {
                return;
            }

            m_mapping.dispatch(new Mapping.Switch() {
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
                public void onStatic(Static s) {
                    if (s.isPrimitive()) {
                        Path[] paths = Code.paths
                            (m_mapping.getObjectMap().getObjectType(), null);
                        String[] cols = Code.columns
                            (paths, m_mapping.getObjectMap().getRetrieveAll());
                        m_key = new Key(m_frame, cols);
                    }
                }
                public void onNested(Nested n) {
                    throw new Error("nested get");
                }
            });
        }

        private void conditions(Constraint c) {
            String[] columns = Code.columns(c, m_expr.getMap(), null);
            m_key = new Key(m_frame, columns);
        }

        public String toString() {
            return emit(m_frame.getGenerator()).getSQL();
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
                values.add(m_frame.getValue(m_columns[i]));
            }
            frame.setValues(values);
        }

        Code emit(Generator gen) {
            return gen.getFrame(this).emit();
        }

        void hash(Generator gen) {
            throw new UnsupportedOperationException();
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
                gen.frame(this, Define.define("this", m_frame.getMap()));
            ths.setValues(m_frame.getValues());
            Expression expr = m_frame.getExpression();
            gen.addNulls(this, gen.getNull(expr));
            gen.addNonNulls(this, gen.getNonNull(expr));
        }

        Code emit(Generator gen) {
            return gen.getFrame(this).emit();
        }

        void hash(Generator gen) {
            throw new UnsupportedOperationException();
        }

        String summary() {
            return "this";
        }

        public String toString() {
            return "this(" + m_frame.getExpression() + ")";
        }

    }

    private static class TableAll extends Expression {

        private String m_table;
        private String[] m_columns;
        private ObjectMap m_map;

        TableAll(String table, String[] columns, ObjectMap map) {
            m_table = table;
            m_columns = columns;
            m_map = map;
        }

        void frame(Generator gen) {
            QFrame frame = gen.frame(this, m_map);
            frame.setValues(m_columns);
            frame.setTable(m_table);
        }

        Code emit(Generator gen) {
            return gen.getFrame(this).emit();
        }

        void hash(Generator gen) {
            throw new UnsupportedOperationException();
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
