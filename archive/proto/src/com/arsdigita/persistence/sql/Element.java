/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.sql;

import com.arsdigita.persistence.PersistenceException;

import java.util.*;
import java.io.*;

/**
 * Element
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public abstract class Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/sql/Element.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private static final Map s_cache = new HashMap();

    public static synchronized Element parse(String sql) {
        Element result = (Element) s_cache.get(sql);

        if (result == null) {
            Parser p = new Parser(new StringReader(sql));
            try {
                result = p.sql();
            } catch (TokenMgrError e) {
                throw PersistenceException.newInstance(
                                                       "Bad SQL: " + sql, e
                                                       );
            } catch (ParseException e) {
                throw PersistenceException.newInstance(
                                                       "Bad SQL: " + sql, e
                                                       );
            }
            s_cache.put(sql, result);
        }

        return result;
    }

    public boolean isIdentifier() {
        return this instanceof Identifier;
    }

    public boolean isBindVar() {
        if (isIdentifier()) {
            return ((Identifier) this).isBindVar();
        } else {
            return false;
        }
    }

    public boolean isStatement() {
        return this instanceof Statement;
    }

    public boolean isUpdate() {
        if (isStatement()) {
            Statement stmt = (Statement) this;
            Clause first = stmt.getFirstClause();
            return first != null && first.isUpdate();
        } else {
            return false;
        }
    }

    public boolean isInsert() {
        if (isStatement()) {
            Statement stmt = (Statement) this;
            Clause first = stmt.getFirstClause();
            return first != null && first.isInsert();
        } else {
            return false;
        }
    }

    public boolean isSelect() {
        if (isStatement()) {
            Statement stmt = (Statement) this;
            Clause first = stmt.getFirstClause();
            return first != null && first.isSelect();
        } else {
            return false;
        }
    }

    public boolean isSelectForUpdate() {
        if (isStatement()) {
            Statement stmt = (Statement) this;
            Clause last = stmt.getLastClause();
            return isSelect() && last.isForUpdate();
        } else {
            return false;
        }
    }

    /**
     * Keywords which indicate DDL.
     **/

    private static final Set s_DDLKeywords = new HashSet(11);
    static {
        s_DDLKeywords.add("create");
        s_DDLKeywords.add("alter");
        s_DDLKeywords.add("grant");
        s_DDLKeywords.add("revoke");
        s_DDLKeywords.add("drop");
        s_DDLKeywords.add("truncate");
        s_DDLKeywords.add("audit");
        s_DDLKeywords.add("noaudit");
        s_DDLKeywords.add("analyze");
        s_DDLKeywords.add("rename");
        s_DDLKeywords.add("comment");
    }

    public boolean isDDL() {
        List elements = getLeafElements();
        if (elements.size() > 0) {
            return s_DDLKeywords.contains(
                                          elements.get(0).toString().toLowerCase()
                                          );
        } else {
            return false;
        }
    }

    public abstract boolean isLeaf();

    public List getLeafElements() {
        List result = new ArrayList();
        addLeafElements(result);
        return result;
    }

    public void output(SQLWriter result, Transformer tran) {
        if (tran.transform(this, result)) {
            return;
        } else {
            makeString(result, tran);
        }
    }

    abstract public void addLeafElements(List l);

    abstract void makeString(SQLWriter result, Transformer tran);

    // This stores the cached string representation of this element.
    private String m_string = null;

    protected final void flush() {
        m_string = null;
    }

    public final String toString() {
        if (m_string == null) {
            SQLWriter result = new SQLWriter();
            output(result, NOOP);
            m_string = result.toString();
        }

        return m_string;
    }

    public abstract void traverse(Visitor v);

    public static interface Transformer {
        boolean transform(Element el, SQLWriter result);
    }

    public static interface Visitor {
        void visit(Element el);
    }

    private static final Transformer NOOP = new Transformer() {
            public boolean transform(Element el, SQLWriter result) {
                return false;
            }
        };

}