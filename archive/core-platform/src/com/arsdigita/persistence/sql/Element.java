package com.arsdigita.persistence.sql;

import com.arsdigita.persistence.PersistenceException;

import java.util.*;
import java.io.*;

/**
 * Element
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/05/30 $
 **/

public abstract class Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Element.java#2 $ by $Author: rhs $, $DateTime: 2002/05/30 15:15:09 $";

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

    public static interface Transformer {
        boolean transform(Element el, SQLWriter result);
    }

    private static final Transformer NOOP = new Transformer() {
            public boolean transform(Element el, SQLWriter result) {
                return false;
            }
        };

}
