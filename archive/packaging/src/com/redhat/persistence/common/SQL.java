package com.redhat.persistence.common;

import java.util.*;

/**
 * SQL
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class SQL {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/common/SQL.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private SQLToken m_first;
    private SQLToken m_last;

    public SQL() {
        m_first = null;
        m_last = null;
    }

    public void append(SQLToken sql) {
        if (m_first == null) {
            m_first = sql;
            m_last = sql;
        } else {
            m_last.m_next = sql;
            sql.m_previous = m_last;
            m_last = sql;
        }
    }

    public SQLToken getFirst() {
        return m_first;
    }

    public SQLToken getLast() {
        return m_last;
    }

    public List getBindings() {
        return getBindings(m_first, null);
    }

    public static final List getBindings(SQLToken start, SQLToken end) {
        ArrayList result = new ArrayList();
        for (SQLToken t = start; t != end; t = t.getNext()) {
            if (t.isBind()) {
                result.add(Path.get(t.getImage()));
            }
        }
        return result;
    }

    public String toString() {
        return toString(m_first, null);
    }

    public static final String toString(SQLToken start, SQLToken end) {
        StringBuffer result = new StringBuffer();
        for (SQLToken t = start; t != end; t = t.getNext() ) {
            result.append(t.getImage());
        }
        return result.toString();
    }

}
