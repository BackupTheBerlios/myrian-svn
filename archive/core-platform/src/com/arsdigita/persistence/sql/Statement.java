package com.arsdigita.persistence.sql;

import java.util.*;

/**
 * Statement
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class Statement extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Statement.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private List m_clauses = new ArrayList();
    private List m_clausesNoMod = Collections.unmodifiableList(m_clauses);

    // Cache the results of makeString. This cached variable is
    // flushed whenever we modify this object.
    private String m_textString;

    public void addClause(Clause clause) {
        m_clauses.add(clause);
        flushCache();
    }

    public Iterator getClauses() {
        return m_clausesNoMod.iterator();
    }

    public boolean isLeaf() {
        return false;
    }

    public Clause getFirstClause() {
	if (m_clauses.size() > 0) {
	    return (Clause) m_clauses.get(0);
	} else {
	    return null;
	}
    }

    public Clause getLastClause() {
	if (m_clauses.size() == 0) {
	    return null;
	} else {
	    return (Clause) m_clauses.get(m_clauses.size() - 1);
	}
    }

    public void addLeafElements(List l) {
        Clause clause;
        for (Iterator it = getClauses(); it.hasNext(); ) {
            clause = (Clause) it.next();
            clause.addLeafElements(l);
        }
        flushCache();
    }

    String makeString() {
        if (m_textString == null) {
            StringBuffer result = new StringBuffer();

            Clause clause;
            for (Iterator it = getClauses(); it.hasNext(); ) {
                clause = (Clause) it.next();
                result.append(clause);
                if (it.hasNext()) {
                    result.append("\n");
                }
            }
            m_textString = result.toString();
        }
        return m_textString;
    }


    private void flushCache() {
        m_textString = null;
    }
}
