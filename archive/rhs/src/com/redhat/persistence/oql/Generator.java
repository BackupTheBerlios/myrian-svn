/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.commons.collections.primitives.*;
import org.apache.log4j.Logger;

/**
 * Generator
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/05/02 $
 **/

class Generator {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/Generator.java#2 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private static final Logger s_log = Logger.getLogger(Generator.class);

    private List m_framepool = new ArrayList();
    private Map m_queries = new HashMap();
    private LinkedList m_stack = new LinkedList();
    private Set m_boolean = new HashSet();
    private MultiMap m_equalities = new MultiMap();
    private Set m_sufficient = new HashSet();
    private MultiMap m_uses = new MultiMap();
    private MultiMap m_null = new MultiMap();
    private MultiMap m_nonnull = new MultiMap();
    private Map m_substitutions = new HashMap();
    private EquiSet m_sharedframes = new EquiSet(this);

    // hash related
    private CharList m_hash = new ArrayCharList();
    private int m_hashCode = 0;
    private Map m_classes = new HashMap();
    private Map m_bindings = new HashMap();
    private Map m_ids = new HashMap();
    private Key m_key = new Key(m_hash, m_hashCode);

    // measures how many levels of qualii we've recursed
    int level = 0;

    private static final class Key {
        private CharList m_hash;
        private int m_code;
        Key(CharList hash, int code) {
            m_hash = hash;
            m_code = code;
        }
        void setCode(int code) {
            m_code = code;
        }
        public int hashCode() {
            return m_code;
        }
        public boolean equals(Object o) {
            Key k = (Key) o;
            if (m_code != k.m_code) {
                return false;
            } else {
                return m_hash.equals(k.m_hash);
            }
        }
    }

    private Root m_root;
    private List m_frames;

    Generator() {}

    void init(Root root) {
        m_root = root;
        m_frames = m_framepool.subList(0, 0);

        m_queries.clear();
        m_stack.clear();
        m_boolean.clear();
        m_equalities.clear();
        m_sufficient.clear();
        m_uses.clear();
        m_null.clear();
        m_nonnull.clear();
        m_substitutions.clear();
        m_sharedframes.clear();

        m_hash.clear();
        m_hashCode = 0;
        m_bindings.clear();
        m_ids.clear();

        level = 0;
    }

    Root getRoot() {
        return m_root;
    }

    CharList getHash() {
        return m_hash;
    }

    int getHashCode() {
        return m_hashCode;
    }

    Object getLookupKey() {
        m_key.setCode(m_hashCode);
        return m_key;
    }

    Object getStoreKey() {
        return new Key(new ArrayCharList(m_hash), m_hashCode);
    }

    Map getBindings() {
        return m_bindings;
    }

    private void appendHash(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            appendHash(str.charAt(i));
        }
    }

    private static final char TERMINAL = '\0';

    private void appendHash(char c) {
        if (c == TERMINAL) {
            m_hash.add(TERMINAL);
        }
        m_hashCode *= 31;
        m_hashCode += c;
        m_hash.add(c);
    }

    private void terminal() {
        m_hash.add(TERMINAL);
    }

    void hash(Class klass) {
        String code = (String) m_classes.get(klass);
        if (code == null) {
            code = "c" + m_classes.size();
            m_classes.put(klass, code);
        }
        appendHash(code);
        terminal();
    }

    void hash(String str) {
        appendHash("s");
        appendHash(str);
        terminal();
    }

    void hash(ObjectType type) {
        appendHash("t");
        appendHash(Integer.toString(System.identityHashCode(type.getRoot())));
        terminal();
        appendHash(type.getQualifiedName());
        terminal();
    }

    void hash(boolean b) {
        appendHash("b");
        if (b) {
            appendHash("1");
        } else {
            appendHash("0");
        }
        terminal();
    }

    void hash(int i) {
        appendHash("i");
        appendHash(Integer.toString(i));
        terminal();
    }

    void hash(SQL sql) {
        appendHash("S");
        for (SQLToken t = sql.getFirst(); t != null; t = t.getNext() ) {
            appendHash(t.getImage());
        }
        terminal();
    }

    Object id(Expression expr) {
        Object id = m_ids.get(expr);
        if (id == null) {
            id = new Integer(m_ids.size());
            m_ids.put(expr, id);
        }
        return id;
    }

    void bind(Code code) {
        List bindings = code.getBindings();
        for (int i = 0; i < bindings.size(); i++) {
            Code.Binding b = (Code.Binding) bindings.get(i);
            setBinding(b.getKey(), b.getValue());
        }
    }

    void setBinding(Object key, Object value) {
        m_bindings.put(key, value);
    }

    Object getBinding(Object key) {
        return m_bindings.get(key);
    }

    List getFrames() {
        return m_frames;
    }

    QFrame frame(Expression expr, ObjectType type) {
        int size = m_frames.size();
        if (size == m_framepool.size()) {
            m_framepool.add(new QFrame(this));
        }
        m_frames = m_framepool.subList(0, size + 1);
        QFrame result = (QFrame) m_frames.get(size);
        result.init(expr, type, peek());
        m_queries.put(expr, result);
        return result;
    }

    QFrame getFrame(Expression e) {
        QFrame result = (QFrame) m_queries.get(e);
        if (result == null) {
            throw new IllegalStateException
                ("no qframe for expression: " + e);
        } else {
            return result;
        }
    }

    boolean hasFrame(Expression e) {
        return m_queries.containsKey(e);
    }

    void push(QFrame frame) {
        m_stack.addFirst(frame);
    }

    QFrame peek() {
        if (m_stack.isEmpty()) {
            return null;
        } else {
            return (QFrame) m_stack.getFirst();
        }
    }

    QFrame pop() {
        return (QFrame) m_stack.removeFirst();
    }

    QFrame resolve(String name) {
        for (Iterator it = m_stack.iterator(); it.hasNext(); ) {
            QFrame frame = (QFrame) it.next();
            if (frame.getType().hasProperty(name)) {
                return frame;
            }
        }

        throw new IllegalArgumentException
            ("unable to resolve variable: " + name + "\n" + getTrace());
    }

    String getTrace() {
        StringBuffer result = new StringBuffer();
        for (Iterator it = m_stack.iterator(); it.hasNext(); ) {
            QFrame frame = (QFrame) it.next();
            result.append(frame.getType());
            if (it.hasNext()) {
                result.append("  \n");
            }
        }
        return result.toString();
    }

    boolean hasType(String name) {
        return m_root.getObjectType(name) != null;
    }

    ObjectType getType(String name) {
        ObjectType result = m_root.getObjectType(name);
        if (result == null) {
            throw new IllegalArgumentException
                ("unable to resolve type: " + name);
        }
        return result;
    }

    void addBoolean(Expression expr) {
        m_boolean.add(expr);
    }

    boolean isBoolean(Expression expr) {
        return m_boolean.contains(expr);
    }

    List getEqualities(Expression expr) {
        return m_equalities.get(expr);
    }

    void addEquality(Expression expr, QValue a, QValue b) {
        m_equalities.add(expr, new Equality(a, b));
    }

    void addEqualities(Expression expr, List equalities) {
        m_equalities.addAll(expr, equalities);
    }

    boolean isSufficient(Expression expr) {
        return m_sufficient.contains(expr);
    }

    void addSufficient(Expression expr) {
        m_sufficient.add(expr);
    }

    List getUses(Expression expr) {
        return m_uses.get(expr);
    }

    void addUse(Expression expr, QValue v) {
        m_uses.add(expr, v);
    }

    void addUses(Expression expr, List values) {
        m_uses.addAll(expr, values);
    }

    List getNull(Expression expr) {
        return m_null.get(expr);
    }

    void addNull(Expression expr, QValue v) {
        m_null.add(expr, v);
    }

    void addNulls(Expression expr, List values) {
        m_null.addAll(expr, values);
    }

    List getNonNull(Expression expr) {
        return m_nonnull.get(expr);
    }

    void addNonNull(Expression expr, QValue v) {
        m_nonnull.add(expr, v);
    }

    void addNonNulls(Expression expr, List values) {
        m_nonnull.addAll(expr, values);
    }

    void setSubstitute(Expression expr, Expression substitute) {
        m_substitutions.put(expr, substitute);
    }

    Expression getSubstitute(Expression expr) {
        return (Expression) m_substitutions.get(expr);
    }

    EquiSet getSharedFrames() {
        return m_sharedframes;
    }

    private static class Equality {
        private QValue m_left;
        private QValue m_right;
        Equality(QValue left, QValue right) {
            m_left = left;
            m_right = right;
        }
        QValue getLeft() {
            return m_left;
        }
        QValue getRight() {
            return m_right;
        }
        QValue getValue(QFrame frame) {
            if (m_left.getFrame().equals(frame)) {
                return m_left;
            } else if (m_right.getFrame().equals(frame)) {
                return m_right;
            } else {
                return null;
            }
        }
        QValue getExternal(QFrame frame) {
            QFrame root = frame.getRoot();
            if (m_left.getFrame().getRoot().equals(root)) {
                if (m_right.getFrame().getRoot().equals(root)) {
                    return null;
                } else {
                    return m_right;
                }
            } else {
                return m_left;
            }
        }
        QValue getOther(QValue value) {
            if (m_left.equals(value)) {
                return m_right;
            } else {
                return m_left;
            }
        }

        public String toString() {
            return "<equality " + m_left + " = " + m_right + ">";
        }
    }

    private Set m_ccolumns = new HashSet();
    private Set m_cframes = new HashSet();
    private List m_cconds = new ArrayList();

    QFrame getConstraining(QFrame frame) {
        m_cconds.clear();
        m_ccolumns.clear();
        m_cframes.clear();
        frame.addConditions(m_cconds);
        for (int i = 0; i < m_cconds.size(); i++) {
            Expression e = (Expression) m_cconds.get(i);
            addConstraining(e, frame, m_ccolumns, m_cframes);
        }
        if (m_ccolumns.isEmpty() || !frame.isConstrained(m_ccolumns)) {
            return null;
        }
        return frame.getContainer();
    }

    private void addConstraining(Expression e, QFrame frame, Set columns,
                                 Set frames) {
        List equalities = getEqualities(e);
        for (int i = 0; i < equalities.size(); i++) {
            Equality eq = (Equality) equalities.get(i);
            QValue external = eq.getExternal(frame);
            if (external == null) { continue; }
            QFrame ext = external.getFrame();
            // We're already part of the same frame
            if (ext.getRoot().equals(frame.getRoot())) {
                continue;
            }
            // Conditions that correlate to our own subqueries don't count
            if (frame.isSubframe(ext)) {
                continue;
            }
            QValue other = eq.getOther(external);
            columns.add(other.getColumn());
            frames.add(ext);
        }
    }

    boolean isConstrained(String table, Collection columns) {
        Table t = m_root.getTable(table);
        if (t == null) { return false; }
        outer: for (Iterator it = t.getConstraints().iterator();
                    it.hasNext(); ) {
            Object o = it.next();
            if (o instanceof UniqueKey) {
                UniqueKey key = (UniqueKey) o;
                Column[] cols = key.getColumns();
                for (int i = 0; i < cols.length; i++) {
                    if (!columns.contains(cols[i].getName())) {
                        continue outer;
                    }
                }
                return true;
            }
        }
        return false;
    }

    void equate(EquiSet equiset, Expression e) {
        List eqs = getEqualities(e);
        for (int i = 0; i < eqs.size(); i++) {
            Equality eq = (Equality) eqs.get(i);
            equiset.equate(eq.getLeft(), eq.getRight());
        }
    }

    void split(QFrame frame, List equalities, List from, List to) {
        for (int i = 0; i < equalities.size(); i++) {
            Equality eq = (Equality) equalities.get(i);
            QValue left = eq.getLeft();
            QValue right = eq.getRight();
            if (frame.contains(left) && frame.contains(right)) {
                // it's inernal, we don't care about it
                continue;
            }
            if (frame.contains(left)) {
                from.add(left);
                to.add(right);
            } else if (frame.contains(right)) {
                from.add(right);
                to.add(left);
            } else {
                // not sure what this case means
                continue;
            }
        }
    }

}
