package com.arsdigita.persistence.sql;

import com.arsdigita.persistence.PersistenceException;

import java.util.*;
import java.io.*;

/**
 * Element
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public abstract class Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Element.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

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

    abstract public void addLeafElements(List l);

    abstract String makeString();

    // This stores the cached string representation of this element.
    private String m_string = null;

    public final String toString() {
        if (m_string == null || true) {
            m_string = makeString();
        }

        return m_string;
    }

}
