package com.arsdigita.persistence.sql;

import java.util.*;

/**
 * SetClause
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class SetClause extends Clause {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/SetClause.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private List m_assigns = new ArrayList();
    private List m_assignsNoMod = Collections.unmodifiableList(m_assigns);

    // Cache the results of makeString. This cached variable is
    // flushed whenever we modify this object.
    private String m_textString;

    public SetClause() {}

    public void addAssign(Assign assign) {
        m_assigns.add(assign);
    }

    public Iterator getAssigns() {
        return m_assignsNoMod.iterator();
    }

    public void addLeafElements(List l) {
        l.add(Symbol.getInstance("set"));
        for (Iterator it = getAssigns(); it.hasNext(); ) {
            Assign assign = (Assign) it.next();
            assign.addLeafElements(l);
            if (it.hasNext()) {
                l.add(Symbol.getInstance(","));
            }
        }
    }

    String makeString() {
        if (m_textString == null) {
            StringBuffer result = new StringBuffer();

            result.append("set ");

            for (Iterator it = getAssigns(); it.hasNext(); ) {
                result.append(it.next());
                if (it.hasNext()) {
                    result.append(",\n    ");
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
