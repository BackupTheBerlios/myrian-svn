package com.arsdigita.persistence.sql;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Clause
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/18 $
 **/

public class Clause extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Clause.java#3 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    private Symbol m_start;
    private SQL m_sql;

    private static Map s_hashMap = new HashMap();

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
            return m_start.getText().equalsIgnoreCase(start);
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

    void makeString(SQLWriter result, Transformer tran) {
        m_start.output(result, tran);

        if (!isWhere()) {
            result.pushIndent(result.getColumn());
        }

        m_sql.output(result, tran);

        if (!isWhere()) {
            result.popIndent();
        }
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
        return start.toString() + sql.toString();
    }

}
