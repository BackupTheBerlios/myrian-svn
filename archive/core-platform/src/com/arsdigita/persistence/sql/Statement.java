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

import java.util.*;

/**
 * Statement
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/11/14 $
 **/

public class Statement extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Statement.java#5 $ by $Author: rhs $, $DateTime: 2002/11/14 18:09:55 $";

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

    public void traverse(Visitor v) {
        v.visit(this);
        for (Iterator it = m_clauses.iterator(); it.hasNext(); ) {
            ((Element) it.next()).traverse(v);
        }
    }

}
