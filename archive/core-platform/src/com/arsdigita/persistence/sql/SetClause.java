package com.arsdigita.persistence.sql;

import java.util.*;

/**
 * SetClause
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/05/30 $
 **/

public class SetClause extends Clause {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/SetClause.java#2 $ by $Author: rhs $, $DateTime: 2002/05/30 15:15:09 $";

    private List m_assigns = new ArrayList();
    private List m_assignsNoMod = Collections.unmodifiableList(m_assigns);

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

    void makeString(SQLWriter result, Transformer tran) {
        result.print("set ");

        result.pushIndent(result.getColumn());
        for (Iterator it = getAssigns(); it.hasNext(); ) {
            Element el = (Element) it.next();
            el.output(result, tran);
            if (it.hasNext()) {
                result.println(",");
            }
        }
        result.popIndent();
    }
}
