package com.arsdigita.persistence.sql;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Clause
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class Clause extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Clause.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private Symbol m_start;
    private SQL m_sql;

    private static Map s_hashMap = new HashMap();

    // Cache the results of makeString.
    private String m_textString;

    Clause() {}

    private Clause(Symbol start, SQL sql) {
        m_start = start;
        m_sql = sql;
    }

    public boolean isUpdate() {
        return isClause("update");
    }

    public boolean isInsert() {
	return isClause("insert");
    }

    public boolean isSelect() {
	return isClause("select");
    }

    public boolean isForUpdate() {
	return isClause("for update");
    }

    public boolean isWhere() {
        return isClause("where");
    }

    public boolean isFrom() {
        return isClause("from");
    }

    private boolean isClause(String start) {
        if (m_start == null) {
            return start == null;
        } else {
            return m_start.toString().equalsIgnoreCase(start);
        }
    }

    public boolean isLeaf() {
        return false;
    }

    public void addLeafElements(List l) {
        l.add(m_start);
        m_sql.addLeafElements(l);
    }

    public boolean isSetClause() {
        return this instanceof SetClause;
    }

    String makeString() {
        if (m_textString == null) {
            m_textString = m_start + " " + m_sql;
        }
        return m_textString;
    }

    public static Clause getInstance(Symbol start, SQL sql) {
        String key = generateKey(start, sql);
        Clause returnValue = (Clause) s_hashMap.get(key);
        if (returnValue == null) {
            returnValue = new Clause(start, sql);
            s_hashMap.put(key, returnValue);
        }
        return returnValue;
    }

    private static String generateKey(Symbol start, SQL sql) {
        return start.makeString() + sql.makeString();
    }

}
