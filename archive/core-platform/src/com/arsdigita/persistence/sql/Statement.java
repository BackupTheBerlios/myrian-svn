package com.arsdigita.persistence.sql;

import java.util.*;

/**
 * Statement
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/18 $
 **/

public class Statement extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Statement.java#3 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    private List m_clauses = new ArrayList();
    private List m_clausesNoMod = Collections.unmodifiableList(m_clauses);

    public void addClause(Clause clause) {
        m_clauses.add(clause);
        flush();
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
        flush();
    }

    void makeString(SQLWriter result, Transformer tran) {
        for (Iterator it = getClauses(); it.hasNext(); ) {
            Clause clause = (Clause) it.next();
            clause.output(result, tran);
            if (it.hasNext()) {
                result.println();
            }
        }
    }

}
